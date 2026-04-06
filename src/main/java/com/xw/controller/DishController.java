package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.CustomPlanSaveDTO;
import com.xw.dto.DishReplaceDTO;
import com.xw.entity.Dish;
import com.xw.service.DishService;
import com.xw.service.SearchHistoryService;
import com.xw.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XW
 */
@Tag(name = "菜品与定制方案模块")
@RestController
@RequestMapping("/api/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Operation(summary = "获取可选菜品列表 (可按关键词搜索)")
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestHeader("token") String token) { // 确保能拿到当前登录用户

        // 1. 原本的查询菜品逻辑
        List<DishVO> dishList = dishService.searchDish(keyword);

        // 2. 🌟 如果有搜索词，且查询成功了，异步/同步保存历史记录
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 解析 token 获取 userId (这里根据你实际的 JwtUtils 怎么写的来获取)
            Long userId = 1L; // 伪代码：请替换为你实际获取当前登录用户ID的代码

            // 保存记录
            searchHistoryService.saveOrUpdateHistory(userId, keyword);
        }

        return Result.success(dishList);
    }

    @Operation(summary = "试替换菜品 (含过敏原自动校验)")
    @PostMapping("/replace")
    public Result<DishVO> replaceDish(@RequestBody DishReplaceDTO dto) {
        return dishService.replaceDish(dto);
    }

    @Operation(summary = "确认保存为专属方案")
    @PostMapping("/custom/save")
    public Result<String> saveCustomPlan(@RequestBody CustomPlanSaveDTO dto) {
        return dishService.saveCustomPlan(dto);
    }
}