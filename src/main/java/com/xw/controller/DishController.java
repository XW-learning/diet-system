package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.CustomPlanSaveDTO;
import com.xw.dto.DishReplaceDTO;
import com.xw.entity.Dish;
import com.xw.service.DishService;
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

    @Operation(summary = "获取可选菜品列表 (可按关键词搜索)")
    @GetMapping("/list")
    public Result<List<Dish>> getDishList(@RequestParam(required = false) String keyword) {
        return dishService.getDishList(keyword);
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