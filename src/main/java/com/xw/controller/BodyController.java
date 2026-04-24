package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.dto.BodyRecordDTO;
import com.xw.entity.UserBodyRecord;
import com.xw.service.BodyService;
import com.xw.utils.ThreadLocalUtil;
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
    @LogOperation("保存身材记录")
    @PostMapping("/save")
    public Result<String> saveRecord(@RequestBody BodyRecordDTO dto) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return bodyService.saveRecord(currentUserId, dto);
    }

    @Operation(summary = "获取身材记录列表")
    @GetMapping("/list")
    public Result<java.util.List<UserBodyRecord>> getRecordList() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return bodyService.getRecordList(currentUserId);
    }

    @Operation(summary = "删除身材记录")
    @LogOperation("删除身材记录")
    @DeleteMapping("/delete")
    public Result<String> deleteRecord(@RequestParam Long id) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return bodyService.deleteRecord(userId, id);
    }
}
