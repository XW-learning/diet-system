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

    @Operation(summary = "获取可选菜品列表 (可按分类和关键词搜索)")
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer categoryId, // 🌟 新增接收分类ID
                       @RequestHeader("token") String token) {

        // 1. 调用更新后的 Service 方法
        List<DishVO> dishList = dishService.searchDish(keyword, categoryId);

        // 2. 保存搜索历史逻辑保持不变
        if (keyword != null && !keyword.trim().isEmpty()) {
            Long userId = 1L; // 实际逻辑应解析token获取
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