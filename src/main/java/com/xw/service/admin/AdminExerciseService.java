package com.xw.service.admin;

import com.xw.vo.user.DailyExerciseVO;
import com.xw.vo.user.WeeklyExerciseVO;
import com.xw.vo.user.MonthlyExerciseVO;

import java.time.LocalDate;

public interface AdminExerciseService {

    DailyExerciseVO getDailyAnalysis(Long userId, LocalDate date);

    WeeklyExerciseVO getWeeklyAnalysis(Long userId, LocalDate dateInWeek);

    MonthlyExerciseVO getMonthlyAnalysis(Long userId, String yearMonth);
}
