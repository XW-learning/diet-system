package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.dto.WaterAddDTO;
import com.xw.entity.UserWaterRecord;
import com.xw.service.WaterRecordService;
import com.xw.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XW
 */
@RestController
@RequestMapping("/api/water")
public class WaterRecordController {

    @Autowired
    private WaterRecordService waterRecordService;

    @Operation(summary = "获取用户今日喝水记录")
    @GetMapping("/today")
    public Result<UserWaterRecord> getTodayRecord() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        UserWaterRecord record = waterRecordService.getTodayRecord(currentUserId);
        return Result.success(record);
    }

    @Operation(summary = "添加喝水记录")
    @LogOperation("添加喝水记录")
    @PostMapping("/add")
    public Result<?> addWater(@RequestBody WaterAddDTO dto) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        waterRecordService.addWater(userId, dto);
        return Result.success("饮水打卡成功");
    }
}