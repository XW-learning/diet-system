package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.ExerciseCheckInDTO;
import com.xw.dto.MealCheckInDTO;
import com.xw.entity.CheckIn;
import com.xw.entity.CheckInStat;
import com.xw.service.CheckInService;
import com.xw.vo.AiDishVO;
import com.xw.vo.CheckInDetailVO;
import com.xw.vo.CheckInSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "打卡与追踪模块")
@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @Operation(summary = "1. 获取今日/某日热量看板数据")
    @GetMapping("/summary")
    public Result<CheckInSummaryVO> getSummary(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        // 如果没传日期，默认看当天的
        if (date == null) {
            date = LocalDate.now();
        }
        return checkInService.getSummary(userId, date);
    }

    @Operation(summary = "2. 饮食打卡 (早/中/晚/加餐)")
    @PostMapping("/meal")
    public Result<String> doMealCheckIn(@RequestBody MealCheckInDTO dto) {
        return checkInService.doMealCheckIn(dto);
    }

    @Operation(summary = "3. 运动打卡")
    @PostMapping("/exercise")
    public Result<String> doExerciseCheckIn(@RequestBody ExerciseCheckInDTO dto) {
        return checkInService.doExerciseCheckIn(dto);
    }

    @Operation(summary = "5. 获取某日打卡明细详情 (含饮食与运动)")
    @GetMapping("/detail")
    public Result<CheckInDetailVO> getCheckInDetail(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return checkInService.getCheckInDetail(userId, date);
    }

    @Operation(summary = "6. 获取打卡历史日历列表")
    @GetMapping("/list")
    public Result<List<CheckIn>> getCheckInList(@RequestParam Long userId) {
        return checkInService.getCheckInList(userId);
    }

    @Operation(summary = "7. 获取打卡统计与状态 (连续天数与月度打卡率)")
    @GetMapping("/stat")
    public Result<CheckInStat> getCheckInStat(@RequestParam Long userId) {
        return checkInService.getCheckInStat(userId);
    }
}