package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.service.AdminExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "管理员-用户运动分析")
@RestController
@RequestMapping("/api/admin/exercise")
public class AdminExerciseController {

    @Autowired
    private AdminExerciseService adminExerciseService;

    @Operation(summary = "用户运动分析（按日/周/月）")
    @LogOperation("用户运动分析")
    @GetMapping("/analysis")
    public Result<?> getAnalysis(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam String date) {

        switch (type) {
            case "daily":
                return Result.success(adminExerciseService.getDailyAnalysis(userId, LocalDate.parse(date)));
            case "weekly":
                return Result.success(adminExerciseService.getWeeklyAnalysis(userId, LocalDate.parse(date)));
            case "monthly":
                return Result.success(adminExerciseService.getMonthlyAnalysis(userId, date));
            default:
                return Result.error("无效的分析类型，可选: daily, weekly, monthly");
        }
    }
}
