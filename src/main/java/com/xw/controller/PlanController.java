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
 * 饮食方案控制器
 * 处理饮食方案相关的HTTP请求
 *
 * @author XW
 */
@Tag(name = "饮食方案模块")
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    /**
     * 获取推荐方案列表
     *
     * @return 推荐方案列表
     */
    @Operation(summary = "获取推荐方案列表")
    @GetMapping("/recommend")
    public Result<List<Plan>> getRecommendPlan() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.getRecommendPlan(currentUserId);
    }

    /**
     * 更换方案（随机刷新）
     *
     * @return 新的方案列表
     */
    @Operation(summary = "更换方案")
    @LogOperation("更换方案")
    @PostMapping("/refresh")
    public Result<List<Plan>> refreshPlan() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.refreshPlan(currentUserId);
    }

    /**
     * 获取方案详情（含菜谱）
     *
     * @param planId 方案ID
     * @return 方案详情
     */
    @Operation(summary = "方案详情")
    @GetMapping("/detail")
    public Result<PlanDetailVO> getPlanDetail(@RequestParam Long planId) {
        return planService.getPlanDetail(planId);
    }

    /**
     * 收藏或取消收藏方案
     *
     * @param dto 收藏操作DTO
     * @return 操作结果
     */
    @Operation(summary = "收藏/取消收藏方案")
    @LogOperation("收藏/取消收藏方案")
    @PostMapping("/favorite")
    public Result<String> favoritePlan(@RequestBody PlanFavoriteDTO dto) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.favoritePlan(currentUserId, dto);
    }

    /**
     * 获取收藏列表
     *
     * @return 收藏的方案列表
     */
    @Operation(summary = "收藏列表")
    @GetMapping("/favorites")
    public Result<List<Plan>> getFavoritePlans() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.getFavoritePlans(currentUserId);
    }

    /**
     * 搜索饮食方案
     *
     * @param searchDTO 搜索条件DTO
     * @return 搜索结果列表
     */
    @Operation(summary = "搜索饮食方案")
    @GetMapping("/search")
    public Result<List<PlanVO>> searchPlans(PlanSearchDTO searchDTO) {
        return planService.searchPlans(searchDTO);
    }

    /**
     * 获取我的专属定制方案列表
     *
     * @return 定制方案列表
     */
    @Operation(summary = "获取我的专属定制方案列表")
    @GetMapping("/custom/list")
    public Result<List<com.xw.entity.UserCustomPlan>> getCustomPlans() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return planService.getCustomPlans(currentUserId);
    }
}