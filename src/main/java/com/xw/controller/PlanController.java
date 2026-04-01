package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.PlanFavoriteDTO;
import com.xw.entity.Plan;
import com.xw.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XW
 */
@Tag(name = "饮食方案模块")
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    @Operation(summary = "获取推荐方案列表")
    @GetMapping("/recommend") // 你的新路径
    // 🌟 这里的返回值必须改成 List
    public Result<java.util.List<Plan>> getRecommendPlan(@RequestParam Long userId) {
        return planService.getRecommendPlan(userId);
    }

    @Operation(summary = "更换方案(随机刷新)")
    @PostMapping("/refresh")
    public Result<java.util.List<Plan>> refreshPlan(@RequestParam Long userId) {
        return planService.refreshPlan(userId);
    }

    @Operation(summary = "方案详情(含菜谱)")
    @GetMapping("/detail")
    public Result<com.xw.vo.PlanDetailVO> getPlanDetail(@RequestParam Long planId) {
        return planService.getPlanDetail(planId);
    }

    @Operation(summary = "收藏/取消收藏方案")
    @PostMapping("/favorite")
    public Result<String> favoritePlan(@RequestBody PlanFavoriteDTO dto) {
        return planService.favoritePlan(dto);
    }

    @Operation(summary = "收藏列表")
    @GetMapping("/favorites")
    public Result<java.util.List<Plan>> getFavoritePlans(@RequestParam Long userId) {
        return planService.getFavoritePlans(userId);
    }
}