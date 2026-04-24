package com.xw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xw.common.Result;
import com.xw.dto.ExerciseCheckInDTO;
import com.xw.dto.MealCheckInDTO;
import com.xw.entity.CheckIn;
import com.xw.entity.CheckInStat;
import com.xw.vo.*;

import java.time.LocalDate;
import java.util.List;

public interface CheckInService extends IService<CheckIn> {
    Result<CheckInSummaryVO> getSummary(Long userId, LocalDate date);
    Result<String> doMealCheckIn(Long userId, MealCheckInDTO dto);

    // 🌟 更新：显式接收 userId 参数
    Result<String> doMealCheckInBatch(Long userId, List<MealCheckInDTO> dtoList);
    Result<String> doExerciseCheckIn(Long userId, ExerciseCheckInDTO dto);

    Result<CheckInDetailVO> getCheckInDetail(Long userId, LocalDate date);
    Result<List<CheckIn>> getCheckInList(Long userId);
    Result<CheckInStat> getCheckInStat(Long userId);
    Result<CheckInAnalysisVO> getDailyAnalysis(Long userId, String date);
    Result<FitnessCalendarVO> getFitnessCalendarData(Long userId, Integer year, Integer month);
    Result<FatLossCalendarVO> getFatLossCalendarData(Long userId, Integer year, Integer month);
    Result<FoodCalendarVO> getFoodCalendarData(Long userId, Integer year, Integer month);
}