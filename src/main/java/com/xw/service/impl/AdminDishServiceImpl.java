package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xw.common.PageResult;
import com.xw.dto.AdminDishQueryDTO;
import com.xw.dto.AdminDishSaveDTO;
import com.xw.entity.Category;
import com.xw.entity.Dish;
import com.xw.exception.BusinessException;
import com.xw.mapper.CategoryMapper;
import com.xw.mapper.DishMapper;
import com.xw.service.AdminDishService;
import com.xw.vo.AdminDishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDishServiceImpl implements AdminDishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResult<AdminDishVO> getDishList(AdminDishQueryDTO queryDTO) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(Dish::getName, queryDTO.getKeyword());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Dish::getCategoryId, queryDTO.getCategoryId());
        }
        wrapper.orderByDesc(Dish::getCreateTime);

        Page<Dish> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Dish> dishPage = dishMapper.selectPage(page, wrapper);

        List<AdminDishVO> voList = dishPage.getRecords().stream().map(dish -> {
            AdminDishVO vo = new AdminDishVO();
            BeanUtils.copyProperties(dish, vo);
            if (dish.getCategoryId() != null) {
                Category cat = categoryMapper.selectById(dish.getCategoryId());
                if (cat != null) vo.setCategoryName(cat.getName());
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, dishPage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDish(AdminDishSaveDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        if (dto.getId() != null) {
            Dish existing = dishMapper.selectById(dto.getId());
            if (existing == null) throw new BusinessException("菜品不存在");
            dish.setId(dto.getId());
            dishMapper.updateById(dish);
        } else {
            dish.setCreateTime(LocalDateTime.now());
            dishMapper.insert(dish);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDish(Long dishId) {
        if (dishId == null) throw new BusinessException("菜品ID不能为空");
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) throw new BusinessException("菜品不存在");
        dishMapper.deleteById(dishId);
    }
}
