package com.xw.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.dto.user.ExerciseCalendarDTO;
import com.xw.dto.user.ExerciseCheckInDTO;
import com.xw.dto.user.MealCheckInDTO;
import com.xw.entity.user.*;
import com.xw.entity.admin.*;
import com.xw.entity.ai.*;
import com.xw.exception.BusinessException;
import com.xw.mapper.user.*;
import com.xw.mapper.admin.*;
import com.xw.mapper.ai.*;
import com.xw.service.user.CheckInService;
import com.xw.vo.user.*;
import com.xw.vo.admin.*;
import com.xw.vo.ai.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 打卡业务实现类 - 重构版 (支持普通系统菜品与AI识别食品隔离)
 *
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
    @Autowired
    private CheckInStatMapper checkInStatMapper;
    @Autowired
    private UserWaterRecordMapper userWaterRecordMapper;

    @Override
    public CheckInSummaryVO getSummary(Long userId, LocalDate date) {
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
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String doMealCheckIn(Long userId, MealCheckInDTO dto) {
        // 1. 参数校验
        if (dto.getDishId() == null && dto.getAiRecordId() == null) {
            throw new BusinessException("提交失败：请指定系统菜品或AI识别记录");
        }
        if (dto.getMealType() == null || dto.getWeight() == null) {
            throw new BusinessException("提交失败：缺少餐次或重量参数");
        }

        int cal = 0;
        BigDecimal carb = BigDecimal.ZERO;
        BigDecimal protein = BigDecimal.ZERO;
        BigDecimal fat = BigDecimal.ZERO;
        BigDecimal fiber = BigDecimal.ZERO;
        String finalFoodName = dto.getFoodName();

        // 2. 逻辑分流计算
        if (dto.getAiRecordId() != null) {
            // == AI 食品打卡逻辑 ==
            if (dto.getCalorie() == null) throw new BusinessException("AI打卡缺少卡路里数据");

            BigDecimal inputWeight = new BigDecimal(dto.getWeight());
            BigDecimal refWeight = new BigDecimal("100");
            BigDecimal factor = inputWeight.divide(refWeight, 6, RoundingMode.HALF_UP);

            cal = factor.multiply(new BigDecimal(dto.getCalorie())).setScale(0, RoundingMode.HALF_UP).intValue();
            carb = factor.multiply(dto.getCarbohydrate() != null ? dto.getCarbohydrate() : BigDecimal.ZERO);
            protein = factor.multiply(dto.getProtein() != null ? dto.getProtein() : BigDecimal.ZERO);
            fat = factor.multiply(dto.getFat() != null ? dto.getFat() : BigDecimal.ZERO);
            fiber = BigDecimal.ZERO; // AI暂不提供纤维数据

        } else {
            // == 普通系统菜品打卡逻辑 ==
            Dish dish = dishMapper.selectById(dto.getDishId());
            if (dish == null) throw new BusinessException("系统菜品不存在");

            finalFoodName = dish.getName();
            BigDecimal inputWeight = new BigDecimal(dto.getWeight());
            BigDecimal refWeight = dish.getRefWeight() != null ? dish.getRefWeight() : new BigDecimal("100");
            BigDecimal factor = inputWeight.divide(refWeight, 6, RoundingMode.HALF_UP);

            cal = factor.multiply(new BigDecimal(dish.getCalorie())).setScale(0, RoundingMode.HALF_UP).intValue();
            carb = factor.multiply(dish.getCarbohydrate() != null ? dish.getCarbohydrate() : BigDecimal.ZERO);
            protein = factor.multiply(dish.getProtein() != null ? dish.getProtein() : BigDecimal.ZERO);
            fat = factor.multiply(dish.getFat() != null ? dish.getFat() : BigDecimal.ZERO);
            fiber = factor.multiply(dish.getFiber() != null ? dish.getFiber() : BigDecimal.ZERO);
        }

        // 3. 处理主表记录
        LocalDate targetDate = dto.getDate() != null ? dto.getDate() : LocalDate.now();

        LambdaQueryWrapper<CheckIn> query = new LambdaQueryWrapper<>();
        query.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, targetDate);
        CheckIn mainRecord = checkInMapper.selectOne(query);

        if (mainRecord == null) {
            mainRecord = new CheckIn();
            mainRecord.setUserId(userId);
            mainRecord.setDate(targetDate);
            mainRecord.setBudgetCalorie(calculateDynamicBudget(userId));
            mainRecord.setTotalCalorie(cal);
            mainRecord.setBurnCalorie(0);
            mainRecord.setCreateTime(LocalDateTime.now());

            try {
                checkInMapper.insert(mainRecord);
            } catch (org.springframework.dao.DuplicateKeyException e) {
                mainRecord = checkInMapper.selectOne(query);
                checkInMapper.atomicIncrementCalorie(mainRecord.getId(), cal);
            }
        } else {
            checkInMapper.atomicIncrementCalorie(mainRecord.getId(), cal);
        }

        // 4. 插入明细记录
        CheckInDetail detail = new CheckInDetail();
        detail.setCheckInId(mainRecord.getId());
        detail.setMealType(dto.getMealType());

        // 🌟 分离存储
        detail.setDishId(dto.getDishId());
        detail.setAiRecordId(dto.getAiRecordId());
        detail.setFoodName(finalFoodName);

        detail.setCalorie(cal);
        detail.setCarbohydrate(carb.setScale(1, RoundingMode.HALF_UP));
        detail.setProtein(protein.setScale(1, RoundingMode.HALF_UP));
        detail.setFat(fat.setScale(1, RoundingMode.HALF_UP));
        detail.setFiber(fiber.setScale(1, RoundingMode.HALF_UP));
        detail.setType(dto.getType() != null ? dto.getType() : 2);
        detail.setCreateTime(LocalDateTime.now());

        detailMapper.insert(detail);

        // 5. 更新统计
        if (targetDate.equals(LocalDate.now())) {
            updateCheckInStat(userId, targetDate);
        }

        return "打卡成功！本次摄入 " + cal + " 千卡";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String doMealCheckInBatch(Long userId, List<MealCheckInDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new BusinessException("提交失败：食物列表为空");
        }

        LocalDate targetDate = dtoList.get(0).getDate() != null ? dtoList.get(0).getDate() : LocalDate.now();
        int totalCalorieToAdd = 0;
        List<CheckInDetail> detailList = new ArrayList<>();

        // 🌟 修复后的批量循环逻辑（兼容了AI记录）
        for (MealCheckInDTO dto : dtoList) {
            // 判断是否缺斤少两或者毫无ID
            if (dto.getWeight() == null) continue;
            if (dto.getDishId() == null && dto.getAiRecordId() == null) continue;

            int cal = 0;
            BigDecimal carb = BigDecimal.ZERO, protein = BigDecimal.ZERO, fat = BigDecimal.ZERO, fiber = BigDecimal.ZERO;
            String finalFoodName = dto.getFoodName();

            // AI 分支
            if (dto.getAiRecordId() != null) {
                if (dto.getCalorie() == null) continue; // AI没有卡路里不计入
                BigDecimal inputWeight = new BigDecimal(dto.getWeight());
                BigDecimal refWeight = new BigDecimal("100");
                BigDecimal factor = inputWeight.divide(refWeight, 6, RoundingMode.HALF_UP);

                cal = factor.multiply(new BigDecimal(dto.getCalorie())).setScale(0, RoundingMode.HALF_UP).intValue();
                carb = factor.multiply(dto.getCarbohydrate() != null ? dto.getCarbohydrate() : BigDecimal.ZERO);
                protein = factor.multiply(dto.getProtein() != null ? dto.getProtein() : BigDecimal.ZERO);
                fat = factor.multiply(dto.getFat() != null ? dto.getFat() : BigDecimal.ZERO);
            }
            // 普通菜品分支
            else {
                Dish dish = dishMapper.selectById(dto.getDishId());
                if (dish == null) continue;

                finalFoodName = dish.getName();
                BigDecimal inputWeight = new BigDecimal(dto.getWeight());
                BigDecimal refWeight = dish.getRefWeight() != null ? dish.getRefWeight() : new BigDecimal("100");
                BigDecimal factor = inputWeight.divide(refWeight, 6, RoundingMode.HALF_UP);

                cal = factor.multiply(new BigDecimal(dish.getCalorie())).setScale(0, RoundingMode.HALF_UP).intValue();
                carb = factor.multiply(dish.getCarbohydrate() != null ? dish.getCarbohydrate() : BigDecimal.ZERO);
                protein = factor.multiply(dish.getProtein() != null ? dish.getProtein() : BigDecimal.ZERO);
                fat = factor.multiply(dish.getFat() != null ? dish.getFat() : BigDecimal.ZERO);
                fiber = factor.multiply(dish.getFiber() != null ? dish.getFiber() : BigDecimal.ZERO);
            }

            totalCalorieToAdd += cal;

            // 构建明细对象
            CheckInDetail detail = new CheckInDetail();
            detail.setMealType(dto.getMealType());

            // 🌟 核心：存储正确的 ID 和 名称
            detail.setDishId(dto.getDishId());
            detail.setAiRecordId(dto.getAiRecordId());
            detail.setFoodName(finalFoodName);

            detail.setCalorie(cal);
            detail.setCarbohydrate(carb.setScale(1, RoundingMode.HALF_UP));
            detail.setProtein(protein.setScale(1, RoundingMode.HALF_UP));
            detail.setFat(fat.setScale(1, RoundingMode.HALF_UP));
            detail.setFiber(fiber.setScale(1, RoundingMode.HALF_UP));
            detail.setType(dto.getType() != null ? dto.getType() : 2);
            detail.setCreateTime(LocalDateTime.now());

            detailList.add(detail);
        }

        if (detailList.isEmpty()) {
            throw new BusinessException("打卡失败：无可用的食物数据");
        }

        // 保存主表
        LambdaQueryWrapper<CheckIn> query = new LambdaQueryWrapper<>();
        query.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, targetDate);
        CheckIn mainRecord = checkInMapper.selectOne(query);

        if (mainRecord == null) {
            mainRecord = new CheckIn();
            mainRecord.setUserId(userId);
            mainRecord.setDate(targetDate);
            mainRecord.setBudgetCalorie(calculateDynamicBudget(userId));
            mainRecord.setTotalCalorie(totalCalorieToAdd);
            mainRecord.setBurnCalorie(0);
            mainRecord.setCreateTime(LocalDateTime.now());

            try {
                checkInMapper.insert(mainRecord);
            } catch (org.springframework.dao.DuplicateKeyException e) {
                mainRecord = checkInMapper.selectOne(query);
                checkInMapper.atomicIncrementCalorie(mainRecord.getId(), totalCalorieToAdd);
            }
        } else {
            checkInMapper.atomicIncrementCalorie(mainRecord.getId(), totalCalorieToAdd);
        }

        Long checkInId = mainRecord.getId();
        for (CheckInDetail detail : detailList) {
            detail.setCheckInId(checkInId);
            detailMapper.insert(detail);
        }

        if (targetDate.equals(LocalDate.now())) {
            updateCheckInStat(userId, targetDate);
        }

        return "打卡成功！共计摄入 " + totalCalorieToAdd + " 千卡";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String doExerciseCheckIn(Long userId, ExerciseCheckInDTO dto) {
        if (dto.getExerciseId() == null || dto.getDuration() == null) {
            throw new BusinessException("缺少必要参数");
        }
        LocalDate targetDate = dto.getDate() != null ? dto.getDate() : LocalDate.now();

        Exercise exercise = exerciseMapper.selectById(dto.getExerciseId());
        if (exercise == null) throw new BusinessException("选择的运动项目不存在");

        double caloriePerMinute = exercise.getCaloriePerHalfHour() / 30.0;
        int calculatedBurnCalorie = (int) Math.round(caloriePerMinute * dto.getDuration());

        LambdaQueryWrapper<CheckIn> mainWrapper = new LambdaQueryWrapper<>();
        mainWrapper.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, targetDate);
        CheckIn mainRecord = checkInMapper.selectOne(mainWrapper);

        boolean isFirstCheckInToday = false;

        if (mainRecord == null) {
            mainRecord = new CheckIn();
            mainRecord.setUserId(userId);
            mainRecord.setDate(targetDate);
            mainRecord.setBudgetCalorie(calculateDynamicBudget(userId));
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
        record.setUserId(userId);
        record.setRecordDate(targetDate);
        record.setExerciseName(exercise.getName());
        record.setDuration(dto.getDuration());
        record.setBurnCalorie(calculatedBurnCalorie);
        record.setCreateTime(LocalDateTime.now());
        exerciseRecordMapper.insert(record);

        if (isFirstCheckInToday && targetDate.equals(LocalDate.now())) {
            updateCheckInStat(userId, targetDate);
        }

        return "运动成功！增加了 " + calculatedBurnCalorie + " kcal 额度";
    }

    @Override
    public CheckInDetailVO getCheckInDetail(Long userId, LocalDate date) {
        CheckInDetailVO vo = new CheckInDetailVO();
        vo.setMeals(new ArrayList<>());
        vo.setExercises(new ArrayList<>());

        LambdaQueryWrapper<CheckIn> mainWrapper = new LambdaQueryWrapper<>();
        mainWrapper.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, date);
        CheckIn mainRecord = checkInMapper.selectOne(mainWrapper);

        if (mainRecord == null) return vo;

        vo.setCheckIn(mainRecord);
        // 注意：如果你这行代码报错，可能需要去 mapper XML 文件中确保 join 语句采用了 left join 处理 t_dish 表。
        List<MealVO> meals = detailMapper.getDetailMeals(mainRecord.getId());
        if (meals != null) vo.setMeals(meals);

        LambdaQueryWrapper<ExerciseRecord> exWrapper = new LambdaQueryWrapper<>();
        exWrapper.eq(ExerciseRecord::getCheckInId, mainRecord.getId()).orderByAsc(ExerciseRecord::getCreateTime);
        List<ExerciseRecord> exercises = exerciseRecordMapper.selectList(exWrapper);
        if (exercises != null) vo.setExercises(exercises);

        return vo;
    }

    @Override
    public List<CheckIn> getCheckInList(Long userId) {
        if (userId == null) throw new BusinessException("用户ID不能为空");
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId).orderByDesc(CheckIn::getDate);
        return checkInMapper.selectList(wrapper);
    }

    @Override
    public CheckInStat getCheckInStat(Long userId) {
        if (userId == null) throw new BusinessException("用户ID不能为空");
        LambdaQueryWrapper<CheckInStat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInStat::getUserId, userId);
        CheckInStat stat = statMapper.selectOne(wrapper);
        if (stat == null) {
            stat = new CheckInStat();
            stat.setUserId(userId);
            stat.setContinuousDays(0);
            stat.setMonthRate(BigDecimal.ZERO);
        }
        return stat;
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

    @Override
    public CheckInAnalysisVO getDailyAnalysis(Long userId, String dateStr) {
        LocalDate targetDate = LocalDate.parse(dateStr);
        CheckInAnalysisVO vo = new CheckInAnalysisVO();

        LambdaQueryWrapper<CheckIn> mainQuery = new LambdaQueryWrapper<>();
        mainQuery.eq(CheckIn::getUserId, userId).eq(CheckIn::getDate, targetDate);
        CheckIn mainRecord = checkInMapper.selectOne(mainQuery);

        int budget = 0;
        if (mainRecord == null) {
            budget = calculateDynamicBudget(userId);
            vo.setBudgetCalorie(budget);
            vo.setIntakeCalorie(0);
            vo.setBurnCalorie(0);
            vo.setRemainCalorie(budget);
            vo.setTotalCarbohydrate(BigDecimal.ZERO);
            vo.setTotalProtein(BigDecimal.ZERO);
            vo.setTotalFat(BigDecimal.ZERO);
            vo.setBreakfastCalorie(0);
            vo.setLunchCalorie(0);
            vo.setDinnerCalorie(0);
            vo.setSnackCalorie(0);
        } else {
            budget = mainRecord.getBudgetCalorie() != null ? mainRecord.getBudgetCalorie() : calculateDynamicBudget(userId);
            vo.setBudgetCalorie(budget);
            vo.setIntakeCalorie(mainRecord.getTotalCalorie());
            vo.setBurnCalorie(mainRecord.getBurnCalorie());
            vo.setRemainCalorie(budget - mainRecord.getTotalCalorie() + mainRecord.getBurnCalorie());

            LambdaQueryWrapper<CheckInDetail> detailQuery = new LambdaQueryWrapper<>();
            detailQuery.eq(CheckInDetail::getCheckInId, mainRecord.getId());
            List<CheckInDetail> details = detailMapper.selectList(detailQuery);

            BigDecimal totalCarb = BigDecimal.ZERO;
            BigDecimal totalPro = BigDecimal.ZERO;
            BigDecimal totalFat = BigDecimal.ZERO;
            int bCal = 0, lCal = 0, dCal = 0, sCal = 0;

            for (CheckInDetail detail : details) {
                if (detail.getCarbohydrate() != null) totalCarb = totalCarb.add(detail.getCarbohydrate());
                if (detail.getProtein() != null) totalPro = totalPro.add(detail.getProtein());
                if (detail.getFat() != null) totalFat = totalFat.add(detail.getFat());

                if (detail.getMealType() != null && detail.getCalorie() != null) {
                    switch (detail.getMealType()) {
                        case 1: bCal += detail.getCalorie(); break;
                        case 2: lCal += detail.getCalorie(); break;
                        case 3: dCal += detail.getCalorie(); break;
                        case 4: sCal += detail.getCalorie(); break;
                    }
                }
            }

            vo.setTotalCarbohydrate(totalCarb);
            vo.setTotalProtein(totalPro);
            vo.setTotalFat(totalFat);
            vo.setBreakfastCalorie(bCal);
            vo.setLunchCalorie(lCal);
            vo.setDinnerCalorie(dCal);
            vo.setSnackCalorie(sCal);
        }

        vo.setRecommendCarbohydrate(new BigDecimal(budget * 0.5 / 4).setScale(1, RoundingMode.HALF_UP));
        vo.setRecommendProtein(new BigDecimal(budget * 0.2 / 4).setScale(1, RoundingMode.HALF_UP));
        vo.setRecommendFat(new BigDecimal(budget * 0.3 / 9).setScale(1, RoundingMode.HALF_UP));

        return vo;
    }

    @Override
    public FitnessCalendarVO getFitnessCalendarData(Long userId, Integer year, Integer month) {
        FitnessCalendarVO vo = new FitnessCalendarVO();

        LambdaQueryWrapper<CheckInStat> statWrapper = new LambdaQueryWrapper<>();
        statWrapper.eq(CheckInStat::getUserId, userId);
        CheckInStat stat = checkInStatMapper.selectOne(statWrapper);
        vo.setContinuousDays(stat != null && stat.getContinuousDays() != null ? stat.getContinuousDays() : 0);

        List<ExerciseCalendarDTO> records = exerciseRecordMapper.selectMonthlyExerciseRecords(userId, year, month);

        if (records == null || records.isEmpty()) {
            vo.setTotalWorkoutDays(0);
            vo.setTotalDuration(0);
            vo.setTotalBurnCalorie(0);
            vo.setDailyData(new HashMap<>());
            vo.setTop5Workouts(new ArrayList<>());
            return vo;
        }

        int totalDuration = 0;
        int totalCalories = 0;
        Set<LocalDate> workoutDays = new HashSet<>();

        Map<String, FitnessCalendarVO.DailyFitnessVO> dailyMap = new HashMap<>();
        Map<String, Integer> exerciseDurationMap = new HashMap<>();

        for (ExerciseCalendarDTO record : records) {
            int duration = record.getDuration() != null ? record.getDuration() : 0;
            int calories = record.getBurnCalorie() != null ? record.getBurnCalorie() : 0;

            totalDuration += duration;
            totalCalories += calories;
            workoutDays.add(record.getRecordDate());

            String dateStr = record.getRecordDate().toString();
            FitnessCalendarVO.DailyFitnessVO dailyVO = dailyMap.getOrDefault(dateStr, new FitnessCalendarVO.DailyFitnessVO());
            dailyVO.setDuration((dailyVO.getDuration() == null ? 0 : dailyVO.getDuration()) + duration);
            dailyVO.setCalories((dailyVO.getCalories() == null ? 0 : dailyVO.getCalories()) + calories);
            dailyMap.put(dateStr, dailyVO);

            String exName = record.getExerciseName();
            if (exName != null && !exName.trim().isEmpty()) {
                exerciseDurationMap.put(exName, exerciseDurationMap.getOrDefault(exName, 0) + duration);
            }
        }

        vo.setTotalWorkoutDays(workoutDays.size());
        vo.setTotalDuration(totalDuration);
        vo.setTotalBurnCalorie(totalCalories);
        vo.setDailyData(dailyMap);

        List<FitnessCalendarVO.WorkoutRankVO> top5 = exerciseDurationMap.entrySet().stream()
                .map(entry -> {
                    FitnessCalendarVO.WorkoutRankVO rankVO = new FitnessCalendarVO.WorkoutRankVO();
                    rankVO.setName(entry.getKey());
                    rankVO.setDuration(entry.getValue());
                    return rankVO;
                })
                .sorted((a, b) -> b.getDuration().compareTo(a.getDuration()))
                .limit(5)
                .collect(Collectors.toList());

        vo.setTop5Workouts(top5);

        return vo;
    }

    @Override
    public FatLossCalendarVO getFatLossCalendarData(Long userId, Integer year, Integer month) {
        FatLossCalendarVO vo = new FatLossCalendarVO();
        Map<String, FatLossCalendarVO.DailyFatLossVO> dailyMap = new HashMap<>();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(CheckIn::getUserId, userId)
                .between(CheckIn::getDate, startDate, endDate);
        List<CheckIn> checkIns = checkInMapper.selectList(checkInWrapper);

        LambdaQueryWrapper<UserBodyRecord> bodyWrapper = new LambdaQueryWrapper<>();
        bodyWrapper.eq(UserBodyRecord::getUserId, userId)
                .ge(UserBodyRecord::getRecordTime, startDate.atStartOfDay())
                .le(UserBodyRecord::getRecordTime, endDate.atTime(23, 59, 59));
        List<UserBodyRecord> bodyRecords = bodyRecordMapper.selectList(bodyWrapper);

        LambdaQueryWrapper<UserWaterRecord> waterWrapper = new LambdaQueryWrapper<>();
        waterWrapper.eq(UserWaterRecord::getUserId, userId)
                .between(UserWaterRecord::getRecordDate, startDate, endDate);
        List<UserWaterRecord> waterRecords = userWaterRecordMapper.selectList(waterWrapper);

        for (CheckIn checkIn : checkIns) {
            String dateStr = checkIn.getDate().toString();
            FatLossCalendarVO.DailyFatLossVO daily = dailyMap.computeIfAbsent(dateStr, k -> new FatLossCalendarVO.DailyFatLossVO());

            int intake = checkIn.getTotalCalorie() != null ? checkIn.getTotalCalorie() : 0;
            int burn = checkIn.getBurnCalorie() != null ? checkIn.getBurnCalorie() : 0;
            int budget = checkIn.getBudgetCalorie() != null ? checkIn.getBudgetCalorie() : 0;

            daily.setIntakeCalorie(intake > 0 ? intake : null);

            if (intake > 0) {
                int deficit = budget + burn - intake;
                daily.setDeficitCalorie(deficit > 0 ? deficit : 0);
            }
        }

        for (UserBodyRecord body : bodyRecords) {
            if (body.getWeight() != null) {
                String dateStr = body.getRecordTime().toLocalDate().toString();
                FatLossCalendarVO.DailyFatLossVO daily = dailyMap.computeIfAbsent(dateStr, k -> new FatLossCalendarVO.DailyFatLossVO());
                daily.setWeight(body.getWeight());
            }
        }

        for (UserWaterRecord water : waterRecords) {
            String dateStr = water.getRecordDate().toString();
            FatLossCalendarVO.DailyFatLossVO daily = dailyMap.computeIfAbsent(dateStr, k -> new FatLossCalendarVO.DailyFatLossVO());
            daily.setHasWater(true);
        }

        LambdaQueryWrapper<UserBodyRecord> periodWrapper = new LambdaQueryWrapper<>();
        periodWrapper.eq(UserBodyRecord::getUserId, userId)
                .isNotNull(UserBodyRecord::getPeriodStartDate)
                .isNotNull(UserBodyRecord::getPeriodEndDate)
                .ge(UserBodyRecord::getPeriodEndDate, startDate)
                .le(UserBodyRecord::getPeriodStartDate, endDate);
        List<UserBodyRecord> periodRecords = bodyRecordMapper.selectList(periodWrapper);

        for (UserBodyRecord record : periodRecords) {
            LocalDate pStart = record.getPeriodStartDate();
            LocalDate pEnd = record.getPeriodEndDate();

            LocalDate actualStart = pStart.isBefore(startDate) ? startDate : pStart;
            LocalDate actualEnd = pEnd.isAfter(endDate) ? endDate : pEnd;

            for (LocalDate d = actualStart; !d.isAfter(actualEnd); d = d.plusDays(1)) {
                String dateStr = d.toString();
                FatLossCalendarVO.DailyFatLossVO daily = dailyMap.computeIfAbsent(dateStr, k -> new FatLossCalendarVO.DailyFatLossVO());
                daily.setIsPeriod(true);
            }
        }

        vo.setDailyData(dailyMap);
        return vo;
    }

    @Override
    public FoodCalendarVO getFoodCalendarData(Long userId, Integer year, Integer month) {
        FoodCalendarVO vo = new FoodCalendarVO();
        Map<String, FoodCalendarVO.DailyFoodVO> dailyMap = new HashMap<>();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        LambdaQueryWrapper<CheckIn> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(CheckIn::getUserId, userId)
                .between(CheckIn::getDate, startDate, endDate);
        List<CheckIn> checkIns = checkInMapper.selectList(checkInWrapper);

        if (checkIns.isEmpty()) {
            vo.setDailyData(dailyMap);
            return vo;
        }

        List<Long> checkInIds = checkIns.stream().map(CheckIn::getId).collect(Collectors.toList());

        LambdaQueryWrapper<CheckInDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(CheckInDetail::getCheckInId, checkInIds)
                .isNotNull(CheckInDetail::getMealType);
        List<CheckInDetail> details = detailMapper.selectList(detailWrapper);

        Map<Long, List<CheckInDetail>> detailMap = details.stream()
                .collect(Collectors.groupingBy(CheckInDetail::getCheckInId));

        for (CheckIn checkIn : checkIns) {
            String dateStr = checkIn.getDate().toString();
            FoodCalendarVO.DailyFoodVO daily = new FoodCalendarVO.DailyFoodVO();

            int intake = checkIn.getTotalCalorie() != null ? checkIn.getTotalCalorie() : 0;
            int budget = checkIn.getBudgetCalorie() != null ? checkIn.getBudgetCalorie() : calculateDynamicBudget(userId);
            daily.setIsOver(intake > budget);

            List<String> mealTypes = new ArrayList<>();
            List<CheckInDetail> dayDetails = detailMap.getOrDefault(checkIn.getId(), new ArrayList<>());

            Set<Integer> recordedMeals = dayDetails.stream()
                    .map(CheckInDetail::getMealType)
                    .collect(Collectors.toSet());

            if (recordedMeals.contains(1)) mealTypes.add("breakfast");
            if (recordedMeals.contains(2)) mealTypes.add("lunch");
            if (recordedMeals.contains(3)) mealTypes.add("dinner");
            if (recordedMeals.contains(4)) mealTypes.add("snack");

            daily.setMeals(mealTypes);
            dailyMap.put(dateStr, daily);
        }

        vo.setDailyData(dailyMap);
        return vo;
    }
}