package com.xw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.entity.ExCategory;
import com.xw.service.ExCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 运动分类接口 (用于左侧边栏)
 */
@RestController
@RequestMapping("/api/exCategory")
public class ExCategoryController {

    @Autowired
    private ExCategoryService exCategoryService; // 注入的是 ExCategory 的 Service

    @GetMapping("/list")
    public Result<List<ExCategory>> list() {
        LambdaQueryWrapper<ExCategory> queryWrapper = new LambdaQueryWrapper<>();
        // 按照 sort_order 升序排列
        queryWrapper.orderByAsc(ExCategory::getSortOrder);

        List<ExCategory> list = exCategoryService.list(queryWrapper);
        return Result.success(list);
    }
}