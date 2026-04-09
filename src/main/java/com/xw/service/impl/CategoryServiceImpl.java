package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xw.dto.CategoryDTO;
import com.xw.entity.Category;
import com.xw.mapper.CategoryMapper;
import com.xw.service.CategoryService;
import com.xw.vo.CategoryVO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 默认排序权重兜底处理
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        // BaseMapper 自带 insert
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        // BaseMapper 自带的更新方法叫 updateById
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        // BaseMapper 自带 deleteById
        categoryMapper.deleteById(id);
    }

    @Override
    public List<CategoryVO> getCategoryList() {
        // 使用 MyBatis-Plus 的 LambdaQueryWrapper 实现排序逻辑
        // 按照 sort_order 升序排列，权重一样的按 id 排
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSortOrder)
                .orderByAsc(Category::getId);

        // BaseMapper 自带 selectList
        List<Category> list = categoryMapper.selectList(queryWrapper);

        // 将 Entity 转换为 VO 对象返回给前端
        return list.stream().map(category -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}