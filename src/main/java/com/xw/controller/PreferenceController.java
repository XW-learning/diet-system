package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.UserPreferenceDTO;
import com.xw.entity.UserPreference;
import com.xw.service.PreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XW
 */
@Tag(name = "偏好模块")
@RestController
@RequestMapping("/api/preference")
public class PreferenceController {

    @Autowired
    private PreferenceService preferenceService;

    @Operation(summary = "获取用户饮食偏好")
    @GetMapping("/info")
    public Result<UserPreference> getPreference(@RequestParam Long userId) {
        return preferenceService.getPreference(userId);
    }

    @Operation(summary = "保存饮食偏好")
    @PostMapping("/save")
    public Result<String> savePreference(@RequestBody UserPreferenceDTO dto) {
        return preferenceService.savePreference(dto);
    }

    @Operation(summary = "添加过敏食材")
    @PostMapping("/allergy/add")
    public Result<String> addAllergy(@RequestParam Long userId, @RequestParam Long materialId) {
        return preferenceService.addAllergy(userId, materialId);
    }

    @Operation(summary = "删除过敏食材")
    @DeleteMapping("/allergy/delete")
    public Result<String> deleteAllergy(@RequestParam Long userId, @RequestParam Long materialId) {
        return preferenceService.deleteAllergy(userId, materialId);
    }

    @Operation(summary = "获取用户过敏食材列表")
    @GetMapping("/allergy/list")
    public Result<java.util.List<com.xw.vo.AllergyVO>> getUserAllergies(@RequestParam Long userId) {
        return preferenceService.getUserAllergies(userId);
    }
}