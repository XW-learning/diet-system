package com.xw.service.impl;

import com.xw.dto.ExerciseCalendarDTO;
import com.xw.mapper.ExerciseRecordMapper;
import com.xw.service.AdminExerciseService;
import com.xw.vo.DailyExerciseVO;
import com.xw.vo.WeeklyExerciseVO;
import com.xw.vo.MonthlyExerciseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminExerciseServiceImpl implements AdminExerciseService {

    @Autowired
    private ExerciseRecordMapper exerciseRecordMapper;

    @Override
    public DailyExerciseVO getDailyAnalysis(Long userId, LocalDate date) {
        List<ExerciseCalendarDTO> records = exerciseRecordMapper
                .selectRecordsByDateRange(userId, date, date);

        DailyExerciseVO vo = new DailyExerciseVO();
        vo.setDate(date);
        vo.setExerciseCount(records.size());
        vo.setTotalDuration(records.stream().mapToInt(ExerciseCalendarDTO::getDuration).sum());
        vo.setTotalBurnCalorie(records.stream().mapToInt(ExerciseCalendarDTO::getBurnCalorie).sum());

        List<DailyExerciseVO.ExerciseRecordItem> items = records.stream().map(r -> {
            DailyExerciseVO.ExerciseRecordItem item = new DailyExerciseVO.ExerciseRecordItem();
            item.setExerciseName(r.getExerciseName());
            item.setDuration(r.getDuration());
            item.setBurnCalorie(r.getBurnCalorie());
            return item;
        }).collect(Collectors.toList());
        vo.setRecords(items);

        return vo;
    }

    @Override
    public WeeklyExerciseVO getWeeklyAnalysis(Long userId, LocalDate dateInWeek) {
        LocalDate weekStart = dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        List<ExerciseCalendarDTO> records = exerciseRecordMapper
                .selectRecordsByDateRange(userId, weekStart, weekEnd);

        WeeklyExerciseVO vo = new WeeklyExerciseVO();
        vo.setWeekStart(weekStart);
        vo.setWeekEnd(weekEnd);

        int totalDuration = records.stream().mapToInt(ExerciseCalendarDTO::getDuration).sum();
        int totalCalorie = records.stream().mapToInt(ExerciseCalendarDTO::getBurnCalorie).sum();
        vo.setTotalDuration(totalDuration);
        vo.setTotalBurnCalorie(totalCalorie);

        long workoutDays = records.stream()
                .map(ExerciseCalendarDTO::getRecordDate)
                .distinct()
                .count();
        vo.setTotalWorkoutDays((int) workoutDays);
        vo.setAvgDailyDuration(workoutDays > 0 ? Math.round(totalDuration * 10.0 / workoutDays) / 10.0 : 0.0);
        vo.setAvgDailyCalorie(workoutDays > 0 ? Math.round(totalCalorie * 10.0 / workoutDays) / 10.0 : 0.0);

        Map<LocalDate, List<ExerciseCalendarDTO>> dayGroup = records.stream()
                .collect(Collectors.groupingBy(ExerciseCalendarDTO::getRecordDate));

        List<WeeklyExerciseVO.DailyBreakdown> dailyList = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            LocalDate d = weekStart.plusDays(i);
            WeeklyExerciseVO.DailyBreakdown bd = new WeeklyExerciseVO.DailyBreakdown();
            bd.setDate(d);
            bd.setDayOfWeek(i + 1);

            List<ExerciseCalendarDTO> dayRecords = dayGroup.get(d);
            if (dayRecords != null) {
                bd.setDuration(dayRecords.stream().mapToInt(ExerciseCalendarDTO::getDuration).sum());
                bd.setBurnCalorie(dayRecords.stream().mapToInt(ExerciseCalendarDTO::getBurnCalorie).sum());
            } else {
                bd.setDuration(0);
                bd.setBurnCalorie(0);
            }
            dailyList.add(bd);
        }
        vo.setDailyBreakdown(dailyList);

        List<WeeklyExerciseVO.TopExerciseItem> topList = buildTopExercises(records);
        vo.setTopExercises(topList);

        return vo;
    }

    @Override
    public MonthlyExerciseVO getMonthlyAnalysis(Long userId, String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth);
        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();

        List<ExerciseCalendarDTO> records = exerciseRecordMapper
                .selectRecordsByDateRange(userId, firstDay, lastDay);

        MonthlyExerciseVO vo = new MonthlyExerciseVO();
        vo.setYear(ym.getYear());
        vo.setMonth(ym.getMonthValue());

        int totalDuration = records.stream().mapToInt(ExerciseCalendarDTO::getDuration).sum();
        int totalCalorie = records.stream().mapToInt(ExerciseCalendarDTO::getBurnCalorie).sum();
        vo.setTotalDuration(totalDuration);
        vo.setTotalBurnCalorie(totalCalorie);

        long workoutDays = records.stream()
                .map(ExerciseCalendarDTO::getRecordDate)
                .distinct()
                .count();
        int daysInMonth = ym.lengthOfMonth();
        vo.setTotalWorkoutDays((int) workoutDays);
        vo.setAvgDailyDuration(workoutDays > 0 ? Math.round(totalDuration * 10.0 / daysInMonth) / 10.0 : 0.0);
        vo.setAvgDailyCalorie(workoutDays > 0 ? Math.round(totalCalorie * 10.0 / daysInMonth) / 10.0 : 0.0);

        Map<LocalDate, List<ExerciseCalendarDTO>> dayGroup = records.stream()
                .collect(Collectors.groupingBy(ExerciseCalendarDTO::getRecordDate));

        List<MonthlyExerciseVO.DailyBreakdown> dailyList = new ArrayList<>(daysInMonth);
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate d = firstDay.withDayOfMonth(i);
            MonthlyExerciseVO.DailyBreakdown bd = new MonthlyExerciseVO.DailyBreakdown();
            bd.setDate(d);
            bd.setDay(i);

            List<ExerciseCalendarDTO> dayRecords = dayGroup.get(d);
            if (dayRecords != null) {
                bd.setDuration(dayRecords.stream().mapToInt(ExerciseCalendarDTO::getDuration).sum());
                bd.setBurnCalorie(dayRecords.stream().mapToInt(ExerciseCalendarDTO::getBurnCalorie).sum());
            } else {
                bd.setDuration(0);
                bd.setBurnCalorie(0);
            }
            dailyList.add(bd);
        }
        vo.setDailyBreakdown(dailyList);

        List<MonthlyExerciseVO.TopExerciseItem> topList = new ArrayList<>();
        records.stream()
                .collect(Collectors.groupingBy(ExerciseCalendarDTO::getExerciseName))
                .forEach((name, list) -> {
                    MonthlyExerciseVO.TopExerciseItem item = new MonthlyExerciseVO.TopExerciseItem();
                    item.setExerciseName(name);
                    item.setCount(list.size());
                    item.setTotalDuration(list.stream().mapToInt(ExerciseCalendarDTO::getDuration).sum());
                    item.setTotalBurnCalorie(list.stream().mapToInt(ExerciseCalendarDTO::getBurnCalorie).sum());
                    topList.add(item);
                });
        topList.sort((a, b) -> b.getTotalDuration() - a.getTotalDuration());
        vo.setTopExercises(topList);

        return vo;
    }

    private List<WeeklyExerciseVO.TopExerciseItem> buildTopExercises(List<ExerciseCalendarDTO> records) {
        Map<String, List<ExerciseCalendarDTO>> grouped = records.stream()
                .collect(Collectors.groupingBy(ExerciseCalendarDTO::getExerciseName));

        List<WeeklyExerciseVO.TopExerciseItem> list = new ArrayList<>();
        grouped.forEach((name, recs) -> {
            WeeklyExerciseVO.TopExerciseItem item = new WeeklyExerciseVO.TopExerciseItem();
            item.setExerciseName(name);
            item.setCount(recs.size());
            item.setTotalDuration(recs.stream().mapToInt(ExerciseCalendarDTO::getDuration).sum());
            item.setTotalBurnCalorie(recs.stream().mapToInt(ExerciseCalendarDTO::getBurnCalorie).sum());
            list.add(item);
        });
        list.sort((a, b) -> b.getTotalDuration() - a.getTotalDuration());
        return list;
    }
}