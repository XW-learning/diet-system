package com.xw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.entity.Exercise;
import com.xw.mapper.ExerciseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 运动项目接口 (用于右侧列表)
 */
@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {

    @Autowired
    private ExerciseMapper exerciseMapper; // 注入的是 Exercise 的 Mapper

    /**
     * 根据条件查询运动列表
     * @param categoryId 分类ID (可选)
     * @param keyword 搜索关键词 (可选)
     */
    @GetMapping("/list")
    public Result<List<Exercise>> list(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<Exercise> queryWrapper = new LambdaQueryWrapper<>();

        // 如果有关键词，就模糊搜索名字
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Exercise::getName, keyword);
        } else {
            // 如果没有关键词，就看有没有传分类ID
            if (categoryId != null) {
                queryWrapper.eq(Exercise::getCategoryId, categoryId);
            }
        }

        // 最新创建的排在前面（或者根据你想要的排序规则）
        queryWrapper.orderByDesc(Exercise::getId);

        List<Exercise> list = exerciseMapper.selectList(queryWrapper);
        return Result.success(list);
    }
}