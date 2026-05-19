package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xw.common.PageResult;
import com.xw.dto.AdminPlanMealSaveDTO;
import com.xw.dto.AdminPlanQueryDTO;
import com.xw.dto.AdminPlanSaveDTO;
import com.xw.entity.*;
import com.xw.exception.BusinessException;
import com.xw.mapper.*;
import com.xw.service.AdminPlanService;
import com.xw.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPlanServiceImpl implements AdminPlanService {

    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private PlanMealGroupMapper mealGroupMapper;
    @Autowired
    private PlanMealDishMapper mealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult<AdminPlanVO> getPlanList(AdminPlanQueryDTO queryDTO) {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(Plan::getName, queryDTO.getKeyword())
                    .or()
                    .like(Plan::getPrinciple, queryDTO.getKeyword()));
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Plan::getCategoryId, queryDTO.getCategoryId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Plan::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(Plan::getCreateTime);

        Page<Plan> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Plan> planPage = planMapper.selectPage(page, wrapper);

        List<AdminPlanVO> voList = planPage.getRecords().stream().map(plan -> {
            AdminPlanVO vo = new AdminPlanVO();
            BeanUtils.copyProperties(plan, vo);
            if (plan.getCategoryId() != null) {
                Category cat = categoryMapper.selectById(plan.getCategoryId());
                if (cat != null) vo.setCategoryName(cat.getName());
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, planPage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public AdminPlanDetailVO getPlanDetail(Long planId) {
        if (planId == null) throw new BusinessException("食谱ID不能为空");

        Plan plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("食谱不存在");

        AdminPlanVO planVO = new AdminPlanVO();
        BeanUtils.copyProperties(plan, planVO);
        if (plan.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(plan.getCategoryId());
            if (cat != null) planVO.setCategoryName(cat.getName());
        }

        LambdaQueryWrapper<PlanMealGroup> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(PlanMealGroup::getPlanId, planId).orderByAsc(PlanMealGroup::getSortOrder);
        List<PlanMealGroup> groups = mealGroupMapper.selectList(groupWrapper);

        List<AdminMealGroupVO> mealVOs = new ArrayList<>();
        for (PlanMealGroup group : groups) {
            AdminMealGroupVO groupVO = new AdminMealGroupVO();
            groupVO.setId(group.getId());
            groupVO.setMealType(group.getMealType());
            groupVO.setMealName(group.getMealName());
            groupVO.setSortOrder(group.getSortOrder());

            LambdaQueryWrapper<PlanMealDish> dishWrapper = new LambdaQueryWrapper<>();
            dishWrapper.eq(PlanMealDish::getMealGroupId, group.getId());
            List<PlanMealDish> mealDishes = mealDishMapper.selectList(dishWrapper);

            List<AdminDishItemVO> dishVOs = new ArrayList<>();
            for (PlanMealDish md : mealDishes) {
                Dish dish = dishMapper.selectById(md.getDishId());
                if (dish != null) {
                    AdminDishItemVO dvo = new AdminDishItemVO();
                    dvo.setId(dish.getId());
                    dvo.setName(dish.getName());
                    dvo.setCalorie(dish.getCalorie());
                    dvo.setWeight(md.getWeight());
                    dvo.setWeightUnit(dish.getWeightUnit());
                    dvo.setProtein(dish.getProtein());
                    dvo.setFat(dish.getFat());
                    dvo.setCarbohydrate(dish.getCarbohydrate());
                    dvo.setImageUrl(dish.getImageUrl());
                    dishVOs.add(dvo);
                }
            }
            groupVO.setDishes(dishVOs);
            mealVOs.add(groupVO);
        }

        AdminPlanDetailVO detail = new AdminPlanDetailVO();
        detail.setPlan(planVO);
        detail.setMeals(mealVOs);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlan(AdminPlanSaveDTO dto) {
        Plan plan = new Plan();
        plan.setName(dto.getName());
        plan.setCategoryId(dto.getCategoryId());
        plan.setCalorieMin(dto.getCalorieMin());
        plan.setCalorieMax(dto.getCalorieMax());
        plan.setPrinciple(dto.getPrinciple());

        if (dto.getId() != null) {
            Plan existing = planMapper.selectById(dto.getId());
            if (existing == null) throw new BusinessException("食谱不存在");
            plan.setId(dto.getId());
            planMapper.updateById(plan);
        } else {
            plan.setStatus(1);
            plan.setUsageCount(0);
            plan.setDuration(7);
            plan.setWeightLoss("");
            plan.setTags("");
            plan.setCoverImage("");
            planMapper.insert(plan);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlanStatus(Long planId, Integer status) {
        if (planId == null) throw new BusinessException("食谱ID不能为空");
        if (status == null || (status != 0 && status != 1)) throw new BusinessException("状态参数错误");

        Plan plan = planMapper.selectById(planId);
        if (plan == null) throw new BusinessException("食谱不存在");

        Plan updatePlan = new Plan();
        updatePlan.setId(planId);
        updatePlan.setStatus(status);
        planMapper.updateById(updatePlan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long planId) {
        if (planId == null) throw new BusinessException("食谱ID不能为空");

        planMapper.deleteById(planId);

        LambdaQueryWrapper<PlanMealGroup> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(PlanMealGroup::getPlanId, planId);
        List<PlanMealGroup> groups = mealGroupMapper.selectList(groupWrapper);

        for (PlanMealGroup group : groups) {
            LambdaQueryWrapper<PlanMealDish> dishWrapper = new LambdaQueryWrapper<>();
            dishWrapper.eq(PlanMealDish::getMealGroupId, group.getId());
            mealDishMapper.delete(dishWrapper);
        }
        mealGroupMapper.delete(groupWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePlanMeals(AdminPlanMealSaveDTO dto) {
        if (dto.getPlanId() == null) throw new BusinessException("食谱ID不能为空");

        LambdaQueryWrapper<PlanMealGroup> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(PlanMealGroup::getPlanId, dto.getPlanId());
        List<PlanMealGroup> oldGroups = mealGroupMapper.selectList(groupWrapper);
        for (PlanMealGroup g : oldGroups) {
            LambdaQueryWrapper<PlanMealDish> dishWrapper = new LambdaQueryWrapper<>();
            dishWrapper.eq(PlanMealDish::getMealGroupId, g.getId());
            mealDishMapper.delete(dishWrapper);
        }
        mealGroupMapper.delete(groupWrapper);

        if (dto.getMeals() == null || dto.getMeals().isEmpty()) return;

        for (AdminPlanMealSaveDTO.MealItem item : dto.getMeals()) {
            PlanMealGroup group = new PlanMealGroup();
            group.setPlanId(dto.getPlanId());
            group.setMealName(item.getMealName());
            group.setMealType(item.getMealType());
            group.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : 0);
            mealGroupMapper.insert(group);

            if (item.getDishIds() != null && !item.getDishIds().isEmpty()) {
                for (Long dishId : item.getDishIds()) {
                    PlanMealDish md = new PlanMealDish();
                    md.setMealGroupId(group.getId());
                    md.setDishId(dishId);
                    md.setWeight(BigDecimal.valueOf(100));
                    mealDishMapper.insert(md);
                }
            }
        }
    }

    @Override
    public List<AdminDishOptionVO> getDishOptions(String keyword, Long categoryId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Dish::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        wrapper.orderByAsc(Dish::getName).last("LIMIT 200");

        List<Dish> dishes = dishMapper.selectList(wrapper);
        return dishes.stream().map(d -> {
            AdminDishOptionVO vo = new AdminDishOptionVO();
            vo.setId(d.getId());
            vo.setName(d.getName());
            vo.setCalorie(d.getCalorie());
            vo.setProtein(d.getProtein());
            vo.setFat(d.getFat());
            vo.setCarbohydrate(d.getCarbohydrate());
            vo.setRefWeight(d.getRefWeight());
            vo.setWeightUnit(d.getWeightUnit());
            return vo;
        }).collect(Collectors.toList());
    }
}
