package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.dto.UserPreferenceDTO;
import com.xw.entity.UserPreference;
import com.xw.service.PreferenceService;
import com.xw.utils.ThreadLocalUtil;
import com.xw.vo.AllergyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<UserPreference> getPreference() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return preferenceService.getPreference(currentUserId);
    }

    @Operation(summary = "保存饮食偏好")
    @LogOperation("保存用户饮食偏好")
    @PostMapping("/save")
    public Result<String> savePreference(@RequestBody UserPreferenceDTO dto) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return preferenceService.savePreference(currentUserId, dto);
    }

    @Operation(summary = "添加过敏食材")
    @LogOperation("添加过敏食材")
    @PostMapping("/allergy/add")
    public Result<String> addAllergy(@RequestParam Long materialId) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return preferenceService.addAllergy(currentUserId, materialId);
    }

    @Operation(summary = "删除过敏食材")
    @LogOperation("删除过敏食材")
    @DeleteMapping("/allergy/delete")
    public Result<String> deleteAllergy(@RequestParam Long materialId) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return preferenceService.deleteAllergy(currentUserId, materialId);
    }

    @Operation(summary = "获取用户过敏食材列表")
    @GetMapping("/allergy/list")
    public Result<List<AllergyVO>> getUserAllergies() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return preferenceService.getUserAllergies(currentUserId);
    }
}