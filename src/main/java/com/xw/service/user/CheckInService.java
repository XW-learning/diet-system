package com.xw.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xw.dto.user.ExerciseCheckInDTO;
import com.xw.dto.user.MealCheckInDTO;
import com.xw.entity.user.CheckIn;
import com.xw.entity.user.CheckInStat;
import com.xw.vo.user.*;
import com.xw.vo.admin.*;
import com.xw.vo.ai.*;

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
