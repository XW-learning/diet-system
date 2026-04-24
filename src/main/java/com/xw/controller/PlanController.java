package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.dto.PlanFavoriteDTO;
import com.xw.dto.PlanSearchDTO;
import com.xw.entity.Plan;
import com.xw.service.PlanService;
import com.xw.utils.ThreadLocalUtil;
import com.xw.vo.PlanDetailVO;
import com.xw.vo.PlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<List<Plan>> getRecommendPlan() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.getRecommendPlan(currentUserId);
    }

    @Operation(summary = "更换方案(随机刷新)")
    @LogOperation("更换方案")
    @PostMapping("/refresh")
    public Result<List<Plan>> refreshPlan() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.refreshPlan(currentUserId);
    }

    @Operation(summary = "方案详情(含菜谱)")
    @GetMapping("/detail")
    public Result<PlanDetailVO> getPlanDetail(@RequestParam Long planId) {
        return planService.getPlanDetail(planId);
    }

    @Operation(summary = "收藏/取消收藏方案")
    @LogOperation("收藏/取消收藏方案")
    @PostMapping("/favorite")
    public Result<String> favoritePlan(@RequestBody PlanFavoriteDTO dto) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.favoritePlan(currentUserId, dto);
    }

    @Operation(summary = "收藏列表")
    @GetMapping("/favorites")
    public Result<List<Plan>> getFavoritePlans() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.getFavoritePlans(currentUserId);
    }

    @Operation(summary = "搜索饮食方案 (一框多搜)")
    @GetMapping("/search")
    public Result<List<PlanVO>> searchPlans(PlanSearchDTO searchDTO) {
        return planService.searchPlans(searchDTO);
    }

    @Operation(summary = "获取我的专属定制方案列表")
    @GetMapping("/custom/list")
    public Result<List<com.xw.entity.UserCustomPlan>> getCustomPlans() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.getCustomPlans(currentUserId);
    }
}