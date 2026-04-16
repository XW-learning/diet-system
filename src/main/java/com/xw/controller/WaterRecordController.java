package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.WaterAddDTO;
import com.xw.entity.UserWaterRecord;
import com.xw.service.WaterRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/water")
public class WaterRecordController {

    @Autowired
    private WaterRecordService waterRecordService;

    @GetMapping("/today")
    public Result<UserWaterRecord> getTodayRecord(@RequestParam Long userId) {
        if (userId == null) {
            return Result.error("缺少用户ID");
        }
        UserWaterRecord record = waterRecordService.getTodayRecord(userId);
        return Result.success(record);
    }

    @PostMapping("/add")
    public Result<?> addWater(@RequestBody WaterAddDTO dto) {
        if (dto.getUserId() == null || dto.getAddAmount() == null || dto.getAddAmount() <= 0) {
            return Result.error("参数不合法");
        }
        waterRecordService.addWater(dto);
        return Result.success("喝水打卡成功");
    }
}