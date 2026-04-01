package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.TargetDTO;
import com.xw.entity.UserTarget;
import com.xw.service.TargetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XW
 */
@Tag(name = "用户目标模块")
@RestController
@RequestMapping("/api/target")
public class TargetController {

    @Autowired
    private TargetService targetService;

    @Operation(summary = "获取用户最新目标")
    @GetMapping("/info")
    public Result<UserTarget> getTarget(@RequestParam Long userId) {
        return targetService.getTarget(userId);
    }

    @Operation(summary = "保存或修改目标")
    @PostMapping("/save")
    public Result<String> saveTarget(@RequestBody TargetDTO dto) {
        return targetService.saveTarget(dto);
    }

    @Operation(summary = "删除用户目标")
    @DeleteMapping("/delete")
    public Result<String> deleteTarget(@RequestParam Long userId) {
        return targetService.deleteTarget(userId);
    }
}