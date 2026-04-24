package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.dto.ExerciseCheckInDTO;
import com.xw.dto.MealCheckInDTO;
import com.xw.entity.CheckIn;
import com.xw.entity.CheckInStat;
import com.xw.service.CheckInService;
import com.xw.utils.ThreadLocalUtil; // 🌟 引入工具类
import com.xw.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡与追踪模块 - 重构版
 * @author XW
 */
@Tag(name = "打卡与追踪模块")
@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @Operation(summary = "1. 获取今日/某日热量看板数据")
    @GetMapping("/summary")
    public Result<CheckInSummaryVO> getSummary(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        // 🌟 直接从 ThreadLocal 获取，前端不再传 userId
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        if (date == null) {
            date = LocalDate.now();
        }
        return checkInService.getSummary(currentUserId, date);
    }

    @Operation(summary = "2. 饮食打卡")
    @LogOperation("饮食打卡")
    @PostMapping("/meal")
    public Result<String> doMealCheckIn(@RequestBody MealCheckInDTO dto) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.doMealCheckIn(currentUserId, dto);
    }

    @Operation(summary = "2.1 批量饮食打卡 (购物车多选)")
    @LogOperation("批量饮食打卡")
    @PostMapping("/meal/batch")
    public Result<String> doMealCheckInBatch(@RequestBody List<MealCheckInDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Result.error("打卡失败：食物列表不能为空");
        }
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.doMealCheckInBatch(currentUserId, dtoList);
    }

    @Operation(summary = "3. 运动打卡")
    @LogOperation("运动打卡")
    @PostMapping("/exercise")
    public Result<String> doExerciseCheckIn(@RequestBody ExerciseCheckInDTO dto) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.doExerciseCheckIn(currentUserId, dto);
    }

    @Operation(summary = "5. 获取某日打卡明细详情 (含饮食与运动)")
    @GetMapping("/detail")
    public Result<CheckInDetailVO> getCheckInDetail(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.getCheckInDetail(currentUserId, date);
    }

    @Operation(summary = "6. 获取打卡历史日历列表")
    @GetMapping("/list")
    public Result<List<CheckIn>> getCheckInList() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.getCheckInList(currentUserId);
    }

    @Operation(summary = "7. 获取打卡统计与状态 (连续天数与月度打卡率)")
    @GetMapping("/stat")
    public Result<CheckInStat> getCheckInStat() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.getCheckInStat(currentUserId);
    }

    @Operation(summary = "3. 获取今日详细饮食分析")
    @GetMapping("/analysis")
    public Result<CheckInAnalysisVO> getDailyAnalysis(@RequestParam("date") String date) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.getDailyAnalysis(currentUserId, date);
    }

    @Operation(summary = "8. 获取健身日历专属聚合数据")
    @GetMapping("/fitness/month")
    public Result<FitnessCalendarVO> getFitnessCalendarData(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.getFitnessCalendarData(currentUserId, year, month);
    }

    @Operation(summary = "减脂日历数据")
    @GetMapping("/fatLoss/month")
    public Result<FatLossCalendarVO> getFatLossCalendarData(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.getFatLossCalendarData(currentUserId, year, month);
    }

    @Operation(summary = "9. 获取美食日历专属聚合数据")
    @GetMapping("/food/month")
    public Result<FoodCalendarVO> getFoodCalendarData(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return checkInService.getFoodCalendarData(currentUserId, year, month);
    }
}