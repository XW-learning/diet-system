package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.ExerciseCheckInDTO;
import com.xw.dto.MealCheckInDTO;
import com.xw.entity.*;
import com.xw.mapper.*;
import com.xw.service.CheckInService;
import com.xw.vo.AiDishVO;
import com.xw.vo.CheckInDetailVO;
import com.xw.vo.CheckInSummaryVO;
import com.xw.vo.MealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XW
 */
@Service
public class CheckInServiceImpl implements CheckInService {

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
    private AiRecognizeMapper aiRecognizeMapper;
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private CheckInStatMapper statMapper;


    @Override
    public Result<CheckInSummaryVO> getSummary(Long userId, LocalDate date) {
        CheckInSummaryVO vo = new CheckInSummaryVO();

        // 1. 去数据库查当天的打卡主表
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, date);
        CheckIn checkIn = checkInMapper.selectOne(wrapper);

        if (checkIn != null) {
            // 场景 A：今天已经打过卡了，直接读取库里的快照数据
            vo.setBudgetCalorie(checkIn.getBudgetCalorie() != null ? checkIn.getBudgetCalorie() : 0);
            vo.setIntakeCalorie(checkIn.getTotalCalorie() != null ? checkIn.getTotalCalorie() : 0);
            vo.setBurnCalorie(checkIn.getBurnCalorie() != null ? checkIn.getBurnCalorie() : 0);
        } else {
            // 场景 B：今天还没打卡，前端依然需要展示“预算”
            // 动态计算用户当前的预算热量，摄入和消耗设为 0
            vo.setBudgetCalorie(calculateDynamicBudget(userId));
            vo.setIntakeCalorie(0);
            vo.setBurnCalorie(0);
        }

        // 2. 计算剩余可吃额度 (核心公式：还可以吃 = 预算 - 饮食 + 运动)
        int remain = vo.getBudgetCalorie() - vo.getIntakeCalorie() + vo.getBurnCalorie();
        // 防御性编程：就算吃超了，页面上显示负数提醒用户，不能报错
        vo.setRemainCalorie(remain);

        return Result.success(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证主明细表一致性
    public Result<String> doMealCheckIn(com.xw.dto.MealCheckInDTO dto) {
        // 1. 基础参数与时间处理
        if (dto.getUserId() == null || dto.getDishId() == null || dto.getMealType() == null) {
            return Result.error("缺少必要参数");
        }
        java.time.LocalDate targetDate = dto.getDate() != null ? dto.getDate() : java.time.LocalDate.now();

        // 2. 查出这道菜的热量
        com.xw.entity.Dish dish = dishMapper.selectById(dto.getDishId());
        if (dish == null) return Result.error("查无此菜品");
        int dishCalorie = dish.getCalorie();

        // 3. 找当天的打卡主表记录
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.xw.entity.CheckIn> mainWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        mainWrapper.eq(com.xw.entity.CheckIn::getUserId, dto.getUserId()).eq(com.xw.entity.CheckIn::getDate, targetDate);
        com.xw.entity.CheckIn mainRecord = checkInMapper.selectOne(mainWrapper);

        boolean isFirstCheckInToday = false; // 🌟 新增标志位：判断是否是今天的第一次行为

        if (mainRecord == null) {
            // == 场景 A：今天第一顿打卡，创建主表 ==
            mainRecord = new com.xw.entity.CheckIn();
            mainRecord.setUserId(dto.getUserId());
            mainRecord.setDate(targetDate);
            // 核心：第一次打卡时，将当天的“预算快照”永久锁死在主表里
            mainRecord.setBudgetCalorie(calculateDynamicBudget(dto.getUserId()));
            mainRecord.setTotalCalorie(dishCalorie);
            mainRecord.setBurnCalorie(0);
            mainRecord.setRemark(dto.getRemark() != null ? dto.getRemark() : "");
            mainRecord.setCreateTime(java.time.LocalDateTime.now());
            checkInMapper.insert(mainRecord);

            isFirstCheckInToday = true; // 🌟 标记为今日首次打卡
        } else {
            // == 场景 B：今天已经打过别的餐了，更新主表 ==
            // 防刷机制：如果同一餐(如午餐)已经打过了，说明用户点错了想替换
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.xw.entity.CheckInDetail> detailWrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            detailWrapper.eq(com.xw.entity.CheckInDetail::getCheckInId, mainRecord.getId())
                    .eq(com.xw.entity.CheckInDetail::getMealType, dto.getMealType());
            com.xw.entity.CheckInDetail oldDetail = detailMapper.selectOne(detailWrapper);

            if (oldDetail != null) {
                // 先扣除以前打错的热量，删掉旧记录
                mainRecord.setTotalCalorie(mainRecord.getTotalCalorie() - oldDetail.getCalorie());
                detailMapper.deleteById(oldDetail.getId());
            }

            // 累加新热量
            mainRecord.setTotalCalorie(mainRecord.getTotalCalorie() + dishCalorie);
            if (dto.getRemark() != null && !dto.getRemark().trim().isEmpty()) {
                mainRecord.setRemark(mainRecord.getRemark() + " | " + dto.getRemark());
            }
            checkInMapper.updateById(mainRecord);
        }

        // 4. 插入本次的新明细
        com.xw.entity.CheckInDetail newDetail = new com.xw.entity.CheckInDetail();
        newDetail.setCheckInId(mainRecord.getId());
        newDetail.setMealType(dto.getMealType());
        newDetail.setDishId(dto.getDishId());
        newDetail.setCalorie(dishCalorie);
        newDetail.setType(dto.getType() != null ? dto.getType() : 2); // 默认算自定义(2)
        newDetail.setCreateTime(java.time.LocalDateTime.now());
        detailMapper.insert(newDetail);

        // 🌟 5. 核心修复触发：如果是今天的第一次真实打卡，去更新连续天数和月度统计！
        if (isFirstCheckInToday && targetDate.equals(java.time.LocalDate.now())) {
            updateCheckInStat(dto.getUserId(), targetDate);
        }

        return Result.success("打卡成功！摄入 " + dishCalorie + " 千卡");
    }

    /**
     * ================= 私有方法 =================
     * 智能动态计算用户当天的推荐热量预算
     */
    private Integer calculateDynamicBudget(Long userId) {
        // 1. 获取最新体重
        LambdaQueryWrapper<UserBodyRecord> bodyWrapper = new LambdaQueryWrapper<>();
        bodyWrapper.eq(UserBodyRecord::getUserId, userId).orderByDesc(UserBodyRecord::getRecordTime).last("LIMIT 1");
        UserBodyRecord body = bodyRecordMapper.selectOne(bodyWrapper);

        // 2. 获取用户目标
        LambdaQueryWrapper<UserTarget> targetWrapper = new LambdaQueryWrapper<>();
        targetWrapper.eq(UserTarget::getUserId, userId).orderByDesc(UserTarget::getCreateTime).last("LIMIT 1");
        UserTarget target = targetMapper.selectOne(targetWrapper);

        // 默认兜底热量
        int baseBudget = 2000;

        if (body != null && body.getWeight() != null) {
            // 粗略的基础代谢计算：体重(kg) * 30 (维持热量)
            BigDecimal weight = body.getWeight();
            baseBudget = weight.multiply(new BigDecimal("30")).intValue();
        }

        // 根据目标制造热量缺口或盈余
        if (target != null && target.getGoalType() != null) {
            if (target.getGoalType().contains("减肥") || target.getGoalType().contains("减脂")) {
                baseBudget -= 400; // 制造 400 大卡的热量缺口
            } else if (target.getGoalType().contains("健身") || target.getGoalType().contains("增肌")) {
                baseBudget += 300; // 制造 300 大卡的热量盈余
            }
        }

        // 保证极端情况下的健康底线（不能低于基础代谢临界点）
        return Math.max(baseBudget, 1200);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> doExerciseCheckIn(com.xw.dto.ExerciseCheckInDTO dto) {
        // 1. 基础参数校验
        if (dto.getUserId() == null || dto.getExerciseId() == null || dto.getDuration() == null) {
            return Result.error("缺少必要参数");
        }
        java.time.LocalDate targetDate = dto.getDate() != null ? dto.getDate() : java.time.LocalDate.now();

        // 2. 后端安全计算：查字典表，算卡路里
        com.xw.entity.Exercise exercise = exerciseMapper.selectById(dto.getExerciseId());
        if (exercise == null) {
            return Result.error("选择的运动项目不存在");
        }

        // 核心公式： (每30分钟热量 / 30.0) * 运动分钟数，四舍五入取整
        double caloriePerMinute = exercise.getCaloriePerHalfHour() / 30.0;
        int calculatedBurnCalorie = (int) Math.round(caloriePerMinute * dto.getDuration());

        // 3. 查询当天的主记录
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.xw.entity.CheckIn> mainWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        mainWrapper.eq(com.xw.entity.CheckIn::getUserId, dto.getUserId()).eq(com.xw.entity.CheckIn::getDate, targetDate);
        com.xw.entity.CheckIn mainRecord = checkInMapper.selectOne(mainWrapper);

        boolean isFirstCheckInToday = false; // 🌟 新增标志位：判断是否是今天的第一次行为

        if (mainRecord == null) {
            // 场景A：今天还没吃任何东西，就先去运动了！也要创建主表！
            mainRecord = new com.xw.entity.CheckIn();
            mainRecord.setUserId(dto.getUserId());
            mainRecord.setDate(targetDate);
            mainRecord.setBudgetCalorie(calculateDynamicBudget(dto.getUserId())); // 锁定今日预算
            mainRecord.setTotalCalorie(0); // 还没吃
            mainRecord.setBurnCalorie(calculatedBurnCalorie); // 记录运动消耗
            mainRecord.setCreateTime(java.time.LocalDateTime.now());
            checkInMapper.insert(mainRecord);

            isFirstCheckInToday = true; // 🌟 标记为今日首次打卡
        } else {
            // 场景B：今天已经打过卡了，累加运动消耗热量
            int currentBurn = mainRecord.getBurnCalorie() != null ? mainRecord.getBurnCalorie() : 0;
            mainRecord.setBurnCalorie(currentBurn + calculatedBurnCalorie);
            checkInMapper.updateById(mainRecord);
        }

        // 4. 插入运动明细记录 (t_exercise_record)
        com.xw.entity.ExerciseRecord record = new com.xw.entity.ExerciseRecord();
        record.setCheckInId(mainRecord.getId());
        record.setExerciseName(exercise.getName()); // 保存运动名称快照
        record.setDuration(dto.getDuration());
        record.setBurnCalorie(calculatedBurnCalorie); // 保存算出来的热量
        record.setCreateTime(java.time.LocalDateTime.now());
        exerciseRecordMapper.insert(record);

        // 🌟 5. 核心修复触发：就算用户没吃饭先运动，只要是今天第一次，也要算他今天打卡成功了！
        if (isFirstCheckInToday && targetDate.equals(java.time.LocalDate.now())) {
            updateCheckInStat(dto.getUserId(), targetDate);
        }

        return Result.success("太棒了！您通过【" + exercise.getName() + "】增加了 " + calculatedBurnCalorie + " kcal 可吃额度！");
    }



    @Override
    public Result<CheckInDetailVO> getCheckInDetail(Long userId, LocalDate date) {
        // 1. 初始化 VO 和空列表，防止前端拿到 null 报 NPE 错误
        CheckInDetailVO vo = new CheckInDetailVO();
        vo.setMeals(new ArrayList<>());
        vo.setExercises(new ArrayList<>());

        // 2. 查当天的打卡主表
        LambdaQueryWrapper<CheckIn> mainWrapper = new LambdaQueryWrapper<>();
        mainWrapper.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, date);
        CheckIn mainRecord = checkInMapper.selectOne(mainWrapper);

        // 🌟 容错处理：如果这天根本没打卡，直接返回空的 VO
        if (mainRecord == null) {
            return Result.success(vo);
        }

        // 存入主记录
        vo.setCheckIn(mainRecord);

        // 3. 查饮食明细 (调用我们在 CheckInDetailMapper 写的联表查询)
        List<MealVO> meals = detailMapper.getDetailMeals(mainRecord.getId());
        if (meals != null) {
            vo.setMeals(meals);
        }

        // 4. 查运动明细 (直接用 MyBatis-Plus 的条件构造器即可)
        LambdaQueryWrapper<ExerciseRecord> exWrapper = new LambdaQueryWrapper<>();
        exWrapper.eq(ExerciseRecord::getCheckInId, mainRecord.getId())
                .orderByAsc(ExerciseRecord::getCreateTime); // 按打卡先后排序
        List<ExerciseRecord> exercises = exerciseRecordMapper.selectList(exWrapper);
        if (exercises != null) {
            vo.setExercises(exercises);
        }

        return Result.success(vo);
    }

    @Override
    public Result<List<CheckIn>> getCheckInList(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 构造查询条件：查该用户所有的主记录，并且按日期从新到旧排列
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId)
                .orderByDesc(CheckIn::getDate);

        // 执行查询
        List<CheckIn> list = checkInMapper.selectList(wrapper);

        return Result.success(list);
    }

    @Override
    public Result<CheckInStat> getCheckInStat(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 1. 直接去统计表里拿现成的数据
        LambdaQueryWrapper<CheckInStat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInStat::getUserId, userId);

        CheckInStat stat = statMapper.selectOne(wrapper);

        // 2. 容错兜底：如果是刚注册的新用户，还没打过卡，返回默认的 0 天和 0%
        if (stat == null) {
            stat = new CheckInStat();
            stat.setUserId(userId);
            stat.setContinuousDays(0);
            stat.setMonthRate(BigDecimal.ZERO);
        }

        return Result.success(stat);
    }

    /**
     * ================= 私有方法：核心打卡统计算法 =================
     * 只有用户每天第一次打卡时，才会触发此方法，保证一天只算一次
     */
    private void updateCheckInStat(Long userId, LocalDate today) {
        // 1. 去统计表里找这个用户的记录
        LambdaQueryWrapper<CheckInStat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInStat::getUserId, userId);

        com.xw.entity.CheckInStat stat = statMapper.selectOne(wrapper);

        boolean isNew = false;
        if (stat == null) {
            stat = new com.xw.entity.CheckInStat();
            stat.setUserId(userId);
            stat.setContinuousDays(0);
            isNew = true;
        }

        // 2. 判断昨天有没有打卡 (决定是断签还是连签)
        LambdaQueryWrapper<com.xw.entity.CheckIn> yesterdayWrapper = new LambdaQueryWrapper<>();
        yesterdayWrapper.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, today.minusDays(1));
        boolean hasYesterday = checkInMapper.selectCount(yesterdayWrapper) > 0;

        // 如果昨天打了卡，连续天数 +1；如果没打卡，重置为 1 (今天算第1天)
        if (hasYesterday) {
            stat.setContinuousDays(stat.getContinuousDays() + 1);
        } else {
            stat.setContinuousDays(1);
        }

        // 3. 计算本月打卡达标率
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate firstDay = currentMonth.atDay(1);
        LocalDate lastDay = currentMonth.atEndOfMonth();

        LambdaQueryWrapper<com.xw.entity.CheckIn> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(CheckIn::getUserId, userId).between(CheckIn::getDate, firstDay, lastDay);
        long daysCheckedIn = checkInMapper.selectCount(monthWrapper);

        int totalDaysInMonth = currentMonth.lengthOfMonth();
        // 公式：打卡天数 / 本月总天数 * 100
        BigDecimal rate = new BigDecimal(daysCheckedIn)
                .divide(new BigDecimal(totalDaysInMonth), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        stat.setMonthRate(rate);
        stat.setUpdateTime(LocalDateTime.now());

        // 4. 最终落库：有则更新，无则插入
        if (isNew) {
            statMapper.insert(stat);
        } else {
            statMapper.updateById(stat);
        }
    }
}