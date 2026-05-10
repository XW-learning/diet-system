package com.xw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xw.dto.ExerciseCheckInDTO;
import com.xw.dto.MealCheckInDTO;
import com.xw.entity.CheckIn;
import com.xw.entity.CheckInStat;
import com.xw.vo.*;

import java.time.LocalDate;
import java.util.List;

public interface CheckInService extends IService<CheckIn> {
    CheckInSummaryVO getSummary(Long userId, LocalDate date);
    String doMealCheckIn(Long userId, MealCheckInDTO dto);
    String doMealCheckInBatch(Long userId, List<MealCheckInDTO> dtoList);
    String doExerciseCheckIn(Long userId, ExerciseCheckInDTO dto);

    CheckInDetailVO getCheckInDetail(Long userId, LocalDate date);
    List<CheckIn> getCheckInList(Long userId);
    CheckInStat getCheckInStat(Long userId);
    CheckInAnalysisVO getDailyAnalysis(Long userId, String date);
    FitnessCalendarVO getFitnessCalendarData(Long userId, Integer year, Integer month);
    FatLossCalendarVO getFatLossCalendarData(Long userId, Integer year, Integer month);
    FoodCalendarVO getFoodCalendarData(Long userId, Integer year, Integer month);
}
