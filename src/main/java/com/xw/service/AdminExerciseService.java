package com.xw.service;

import com.xw.vo.DailyExerciseVO;
import com.xw.vo.WeeklyExerciseVO;
import com.xw.vo.MonthlyExerciseVO;

import java.time.LocalDate;

public interface AdminExerciseService {

    DailyExerciseVO getDailyAnalysis(Long userId, LocalDate date);

    WeeklyExerciseVO getWeeklyAnalysis(Long userId, LocalDate dateInWeek);

    MonthlyExerciseVO getMonthlyAnalysis(Long userId, String yearMonth);
}
