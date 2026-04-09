package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.common.Result;
import com.xw.dto.ExerciseCheckInDTO;
import com.xw.dto.MealCheckInDTO;
import com.xw.entity.*;
import com.xw.mapper.*;
import com.xw.service.CheckInService;
import com.xw.vo.CheckInDetailVO;
import com.xw.vo.CheckInSummaryVO;
import com.xw.vo.MealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * 打卡业务实现类
 * @author XW
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements CheckInService {

    @Autowired
    private CheckInMapper checkInMapper;
    @Autowired
    private CheckInDetailMapper detailMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private UserBodyRecordMapper bodyRecordMapper;
    @Autowired
    private UserTargetMapper targetMapper;
    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private CheckInStatMapper statMapper;

    @Override
    public Result<CheckInSummaryVO> getSummary(Long userId, LocalDate date) {
        CheckInSummaryVO vo = new CheckInSummaryVO();
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, date);
        CheckIn checkIn = checkInMapper.selectOne(wrapper);

        if (checkIn != null) {
            vo.setBudgetCalorie(checkIn.getBudgetCalorie() != null ? checkIn.getBudgetCalorie() : 0);
            vo.setIntakeCalorie(checkIn.getTotalCalorie() != null ? checkIn.getTotalCalorie() : 0);
            vo.setBurnCalorie(checkIn.getBurnCalorie() != null ? checkIn.getBurnCalorie() : 0);
        } else {
            vo.setBudgetCalorie(calculateDynamicBudget(userId));
            vo.setIntakeCalorie(0);
            vo.setBurnCalorie(0);
        }

        vo.setRemainCalorie(vo.getBudgetCalorie() - vo.getIntakeCalorie() + vo.getBurnCalorie());
        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> doMealCheckIn(com.xw.dto.MealCheckInDTO dto) {
        // 1. 严格参数校验
        if (dto.getUserId() == null || dto.getDishId() == null || dto.getMealType() == null || dto.getWeight() == null) {
            return Result.error("提交失败：缺少必要参数");
        }

        // 2. 获取菜品基数并计算（解决精度丢失问题）
        com.xw.entity.Dish dish = dishMapper.selectById(dto.getDishId());
        if (dish == null) return Result.error("菜品不存在");

        // 使用 BigDecimal 确保计算精度，不提前四舍五入
        BigDecimal inputWeight = new BigDecimal(dto.getWeight());
        BigDecimal refWeight = dish.getRefWeight() != null ? dish.getRefWeight() : new BigDecimal("100");
        // 计算系数：当前重量 / 基准重量
        BigDecimal factor = inputWeight.divide(refWeight, 6, RoundingMode.HALF_UP);

        // 计算实际摄入值（保留小数，最后存入数据库时再格式化）
        int cal = factor.multiply(new BigDecimal(dish.getCalorie())).setScale(0, RoundingMode.HALF_UP).intValue();
        BigDecimal carb = factor.multiply(dish.getCarbohydrate() != null ? dish.getCarbohydrate() : BigDecimal.ZERO);
        BigDecimal protein = factor.multiply(dish.getProtein() != null ? dish.getProtein() : BigDecimal.ZERO);
        BigDecimal fat = factor.multiply(dish.getFat() != null ? dish.getFat() : BigDecimal.ZERO);
        BigDecimal fiber = factor.multiply(dish.getFiber() != null ? dish.getFiber() : BigDecimal.ZERO);

        // 3. 处理主表记录 (t_check_in) - 解决并发与初始化问题
        java.time.LocalDate targetDate = dto.getDate() != null ? dto.getDate() : java.time.LocalDate.now();

        // 🌟 核心改进：使用数据库唯一索引 + upsert 逻辑 (需要修改 Mapper)
        // 我们先尝试获取今日记录，如果不存在则初始化
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.xw.entity.CheckIn> query = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        query.eq(com.xw.entity.CheckIn::getUserId, dto.getUserId()).eq(com.xw.entity.CheckIn::getDate, targetDate);
        com.xw.entity.CheckIn mainRecord = checkInMapper.selectOne(query);

        if (mainRecord == null) {
            // 今日首笔记录：初始化预算
            mainRecord = new com.xw.entity.CheckIn();
            mainRecord.setUserId(dto.getUserId());
            mainRecord.setDate(targetDate);
            mainRecord.setBudgetCalorie(calculateDynamicBudget(dto.getUserId())); // 动态计算预算
            mainRecord.setTotalCalorie(cal);
            mainRecord.setBurnCalorie(0);
            mainRecord.setCreateTime(java.time.LocalDateTime.now());

            try {
                checkInMapper.insert(mainRecord);
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // 🌟 解决并发问题：如果 insert 报唯一键冲突，说明另一个请求刚插完，改为更新
                mainRecord = checkInMapper.selectOne(query);
                // 🌟 解决原子累加问题：使用自定义 SQL 累加，防止覆盖
                checkInMapper.atomicIncrementCalorie(mainRecord.getId(), cal);
            }
        } else {
            // 🌟 核心改进：不再删除旧记录（除非业务要求覆盖），而是采用追加逻辑
            // 解决“牛奶+面包”只能记一个的问题
            checkInMapper.atomicIncrementCalorie(mainRecord.getId(), cal);
        }

        // 4. 插入明细记录 (t_check_in_detail)
        com.xw.entity.CheckInDetail detail = new com.xw.entity.CheckInDetail();
        detail.setCheckInId(mainRecord.getId());
        detail.setMealType(dto.getMealType());
        detail.setDishId(dto.getDishId());
        detail.setCalorie(cal);
        detail.setCarbohydrate(carb.setScale(1, RoundingMode.HALF_UP));
        detail.setProtein(protein.setScale(1, RoundingMode.HALF_UP));
        detail.setFat(fat.setScale(1, RoundingMode.HALF_UP));
        detail.setFiber(fiber.setScale(1, RoundingMode.HALF_UP));
        detail.setType(dto.getType() != null ? dto.getType() : 2);
        detail.setCreateTime(java.time.LocalDateTime.now());
        detailMapper.insert(detail);

        // 5. 异步/条件更新统计状态
        // 只要是今天的数据打卡，都触发一次统计校验
        if (targetDate.equals(java.time.LocalDate.now())) {
            updateCheckInStat(dto.getUserId(), targetDate);
        }

        return Result.success("打卡成功！本次摄入 " + cal + " 千卡");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> doExerciseCheckIn(ExerciseCheckInDTO dto) {
        if (dto.getUserId() == null || dto.getExerciseId() == null || dto.getDuration() == null) {
            return Result.error("缺少必要参数");
        }
        LocalDate targetDate = dto.getDate() != null ? dto.getDate() : LocalDate.now();

        Exercise exercise = exerciseMapper.selectById(dto.getExerciseId());
        if (exercise == null) return Result.error("选择的运动项目不存在");

        double caloriePerMinute = exercise.getCaloriePerHalfHour() / 30.0;
        int calculatedBurnCalorie = (int) Math.round(caloriePerMinute * dto.getDuration());

        LambdaQueryWrapper<CheckIn> mainWrapper = new LambdaQueryWrapper<>();
        mainWrapper.eq(CheckIn::getUserId, dto.getUserId()).eq(CheckIn::getDate, targetDate);
        CheckIn mainRecord = checkInMapper.selectOne(mainWrapper);

        boolean isFirstCheckInToday = false;

        if (mainRecord == null) {
            mainRecord = new CheckIn();
            mainRecord.setUserId(dto.getUserId());
            mainRecord.setDate(targetDate);
            mainRecord.setBudgetCalorie(calculateDynamicBudget(dto.getUserId()));
            mainRecord.setTotalCalorie(0);
            mainRecord.setBurnCalorie(calculatedBurnCalorie);
            mainRecord.setCreateTime(LocalDateTime.now());
            checkInMapper.insert(mainRecord);
            isFirstCheckInToday = true;
        } else {
            int currentBurn = mainRecord.getBurnCalorie() != null ? mainRecord.getBurnCalorie() : 0;
            mainRecord.setBurnCalorie(currentBurn + calculatedBurnCalorie);
            checkInMapper.updateById(mainRecord);
        }

        ExerciseRecord record = new ExerciseRecord();
        record.setCheckInId(mainRecord.getId());
        record.setExerciseName(exercise.getName());
        record.setDuration(dto.getDuration());
        record.setBurnCalorie(calculatedBurnCalorie);
        record.setCreateTime(LocalDateTime.now());
        exerciseRecordMapper.insert(record);

        if (isFirstCheckInToday && targetDate.equals(LocalDate.now())) {
            updateCheckInStat(dto.getUserId(), targetDate);
        }

        return Result.success("运动成功！增加了 " + calculatedBurnCalorie + " kcal 额度");
    }

    @Override
    public Result<CheckInDetailVO> getCheckInDetail(Long userId, LocalDate date) {
        CheckInDetailVO vo = new CheckInDetailVO();
        vo.setMeals(new ArrayList<>());
        vo.setExercises(new ArrayList<>());

        LambdaQueryWrapper<CheckIn> mainWrapper = new LambdaQueryWrapper<>();
        mainWrapper.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, date);
        CheckIn mainRecord = checkInMapper.selectOne(mainWrapper);

        if (mainRecord == null) return Result.success(vo);

        vo.setCheckIn(mainRecord);
        List<MealVO> meals = detailMapper.getDetailMeals(mainRecord.getId());
        if (meals != null) vo.setMeals(meals);

        LambdaQueryWrapper<ExerciseRecord> exWrapper = new LambdaQueryWrapper<>();
        exWrapper.eq(ExerciseRecord::getCheckInId, mainRecord.getId()).orderByAsc(ExerciseRecord::getCreateTime);
        List<ExerciseRecord> exercises = exerciseRecordMapper.selectList(exWrapper);
        if (exercises != null) vo.setExercises(exercises);

        return Result.success(vo);
    }

    @Override
    public Result<List<CheckIn>> getCheckInList(Long userId) {
        if (userId == null) return Result.error("用户ID不能为空");
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId).orderByDesc(CheckIn::getDate);
        return Result.success(checkInMapper.selectList(wrapper));
    }

    @Override
    public Result<CheckInStat> getCheckInStat(Long userId) {
        if (userId == null) return Result.error("用户ID不能为空");
        LambdaQueryWrapper<CheckInStat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInStat::getUserId, userId);
        CheckInStat stat = statMapper.selectOne(wrapper);
        if (stat == null) {
            stat = new CheckInStat();
            stat.setUserId(userId);
            stat.setContinuousDays(0);
            stat.setMonthRate(BigDecimal.ZERO);
        }
        return Result.success(stat);
    }

    private Integer calculateDynamicBudget(Long userId) {
        LambdaQueryWrapper<UserBodyRecord> bodyWrapper = new LambdaQueryWrapper<>();
        bodyWrapper.eq(UserBodyRecord::getUserId, userId).orderByDesc(UserBodyRecord::getRecordTime).last("LIMIT 1");
        UserBodyRecord body = bodyRecordMapper.selectOne(bodyWrapper);

        LambdaQueryWrapper<UserTarget> targetWrapper = new LambdaQueryWrapper<>();
        targetWrapper.eq(UserTarget::getUserId, userId).orderByDesc(UserTarget::getCreateTime).last("LIMIT 1");
        UserTarget target = targetMapper.selectOne(targetWrapper);

        int baseBudget = 2000;
        if (body != null && body.getWeight() != null) {
            baseBudget = body.getWeight().multiply(new BigDecimal("30")).intValue();
        }

        if (target != null && target.getGoalType() != null) {
            if (target.getGoalType().contains("减肥") || target.getGoalType().contains("减脂")) {
                baseBudget -= 400;
            } else if (target.getGoalType().contains("健身") || target.getGoalType().contains("增肌")) {
                baseBudget += 300;
            }
        }
        return Math.max(baseBudget, 1200);
    }

    private void updateCheckInStat(Long userId, LocalDate today) {
        LambdaQueryWrapper<CheckInStat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInStat::getUserId, userId);
        CheckInStat stat = statMapper.selectOne(wrapper);

        boolean isNew = false;
        if (stat == null) {
            stat = new CheckInStat();
            stat.setUserId(userId);
            stat.setContinuousDays(0);
            isNew = true;
        }

        LambdaQueryWrapper<CheckIn> yesterdayWrapper = new LambdaQueryWrapper<>();
        yesterdayWrapper.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, today.minusDays(1));
        boolean hasYesterday = checkInMapper.selectCount(yesterdayWrapper) > 0;

        stat.setContinuousDays(hasYesterday ? stat.getContinuousDays() + 1 : 1);

        YearMonth currentMonth = YearMonth.from(today);
        LambdaQueryWrapper<CheckIn> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(CheckIn::getUserId, userId).between(CheckIn::getDate, currentMonth.atDay(1), currentMonth.atEndOfMonth());
        long daysCheckedIn = checkInMapper.selectCount(monthWrapper);

        BigDecimal rate = new BigDecimal(daysCheckedIn)
                .divide(new BigDecimal(currentMonth.lengthOfMonth()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        stat.setMonthRate(rate);
        stat.setUpdateTime(LocalDateTime.now());

        if (isNew) statMapper.insert(stat);
        else statMapper.updateById(stat);
    }
}