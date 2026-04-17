package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.ExerciseCheckInDTO;
import com.xw.dto.MealCheckInDTO;
import com.xw.entity.CheckIn;
import com.xw.entity.CheckInStat;
import com.xw.service.CheckInService;
import com.xw.vo.CheckInDetailVO;
import com.xw.vo.CheckInSummaryVO;
import com.xw.vo.FatLossCalendarVO;
import com.xw.vo.FitnessCalendarVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/meal")
    public Result<String> doMealCheckIn(@RequestBody MealCheckInDTO dto) {
        // 直接校验前端传入的 userId 是否有效
        if (dto.getUserId() == null) {
            return Result.error("打卡失败：用户ID缺失");
        }

        // 如果你依然需要 token 进行登录校验，可以保留 RequestHeader，但不再用它覆盖 userId
        return checkInService.doMealCheckIn(dto);
    }

    @Operation(summary = "2.1 批量饮食打卡 (购物车多选)")
    @PostMapping("/meal/batch")
    public Result<String> doMealCheckInBatch(@RequestBody List<MealCheckInDTO> dtoList, @RequestHeader(value = "token", required = false) String token) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Result.error("打卡失败：食物列表不能为空");
        }

        // 校验首个记录的 userId 是否存在
        if (dtoList.get(0).getUserId() == null) {
            return Result.error("打卡失败：用户ID缺失");
        }

        return checkInService.doMealCheckInBatch(dtoList);
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

    @Operation(summary = "3. 获取今日详细饮食分析(热量+营养素+三餐)")
    @GetMapping("/analysis")
    public Result<com.xw.vo.CheckInAnalysisVO> getDailyAnalysis(
            @RequestParam("userId") Long userId,
            @RequestParam("date") String date) {
        return checkInService.getDailyAnalysis(userId, date);
    }

    @Operation(summary = "8. 获取健身日历专属聚合数据")
    @GetMapping("/fitness/month")
    public Result<FitnessCalendarVO> getFitnessCalendarData(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        // 这里的具体实现将交给 CheckInService
        return checkInService.getFitnessCalendarData(userId, year, month);
    }

    /**
     * 获取减脂日历数据
     */
    @GetMapping("/fatLoss/month")
    public Result<FatLossCalendarVO> getFatLossCalendarData(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return checkInService.getFatLossCalendarData(userId, year, month);
    }
}