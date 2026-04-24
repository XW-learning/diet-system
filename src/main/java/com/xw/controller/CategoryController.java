package com.xw.controller;

import com.xw.annotation.LogOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.xw.common.Result;
import com.xw.dto.CategoryDTO;
import com.xw.service.CategoryService;
import com.xw.vo.CategoryVO;

import java.util.List;

/**
 * @author XW
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "添加分类")
    @LogOperation("添加分类")
    @PostMapping("/add")
    public Result<String> add(@RequestBody CategoryDTO categoryDTO) {
        categoryService.addCategory(categoryDTO);
        return Result.success("添加分类成功");
    }

    @Operation(summary = "修改分类")
    @LogOperation("修改分类")
    @PutMapping("/update")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(categoryDTO);
        return Result.success("修改分类成功");
    }

    @Operation(summary = "删除分类")
    @LogOperation("删除分类")
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return Result.success("删除分类成功");
    }

    /**
     * 获取分类列表 (用户端和管理端均可使用)
     */
    @GetMapping("/list")
    public Result<List<CategoryVO>> list() {
        List<CategoryVO> list = categoryService.getCategoryList();
        return Result.success(list);
    }
}
