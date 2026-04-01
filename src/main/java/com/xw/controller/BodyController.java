package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.BodyRecordDTO;
import com.xw.entity.UserBodyRecord;
import com.xw.service.BodyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XW
 */
@Tag(name = "身材记录模块")
@RestController
@RequestMapping("/api/body")
public class BodyController {

    @Autowired
    private BodyService bodyService;

    @Operation(summary = "保存身材记录(自动算BMI)")
    @PostMapping("/save")
    public Result<String> saveRecord(@RequestBody BodyRecordDTO dto) {
        return bodyService.saveRecord(dto);
    }

    @Operation(summary = "获取身材记录列表")
    @GetMapping("/list")
    public Result<java.util.List<UserBodyRecord>> getRecordList(@RequestParam Long userId) {
        return bodyService.getRecordList(userId);
    }

    @Operation(summary = "删除身材记录")
    @DeleteMapping("/delete")
    public Result<String> deleteRecord(@RequestParam Long id) {
        return bodyService.deleteRecord(id);
    }
}