package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.common.Result;
import com.xw.dto.CustomPlanSaveDTO;
import com.xw.dto.DishReplaceDTO;
import com.xw.entity.Dish;
import com.xw.entity.UserCustomPlan;
import com.xw.entity.UserCustomPlanMeal;
import com.xw.mapper.DishMapper;
import com.xw.mapper.UserCustomPlanMapper;
import com.xw.mapper.UserCustomPlanMealMapper;
import com.xw.service.DishService;
import com.xw.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author XW
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private UserCustomPlanMapper customPlanMapper;

    @Autowired
    private UserCustomPlanMealMapper customPlanMealMapper;

    @Override
    public Result<List<Dish>> getDishList(String keyword) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Dish::getName, keyword.trim());
        }
        wrapper.orderByAsc(Dish::getCalorie);

        List<Dish> dishList = dishMapper.selectList(wrapper);
        return Result.success(dishList);
    }

    @Override
    public List<DishVO> searchDish(String keyword) {
        List<Dish> dishes = this.lambdaQuery()
                .like(keyword != null && !keyword.trim().isEmpty(), Dish::getName, keyword)
                .list();

        return dishes.stream().map(dish -> {
            DishVO vo = new DishVO();
            BeanUtils.copyProperties(dish, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<DishVO> searchDish(String keyword, Integer categoryId) {
        List<Dish> dishes = this.lambdaQuery()
                .like(keyword != null && !keyword.trim().isEmpty(), Dish::getName, keyword)
                .eq(categoryId != null, Dish::getCategoryId, categoryId)
                .list();

        return dishes.stream().map(dish -> {
            DishVO vo = new DishVO();
            BeanUtils.copyProperties(dish, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<DishVO> replaceDish(Long userId, DishReplaceDTO dto) { // 🌟 接收上下文 userId
        // 1. 移除对 dto.getUserId() 的空指针校验
        if (dto.getNewDishId() == null) {
            return Result.error("缺少必要参数");
        }

        // 2. 核心安全防护：过敏原智能检测 (🌟 使用绝对安全的 userId)
        List<String> conflictMaterials = dishMapper.checkAllergyConflict(userId, dto.getNewDishId());
        if (conflictMaterials != null && !conflictMaterials.isEmpty()) {
            String materials = String.join("、", conflictMaterials);
            return Result.error("替换失败！该菜品含有您的过敏食材：" + materials + "，为了您的健康，请重新选择。");
        }

        // 3. 获取新菜品的完整详情（带营养素）
        DishVO newDishInfo = dishMapper.getDishDetailWithNutrition(dto.getNewDishId());

        if (newDishInfo == null) {
            return Result.error("选择的菜品不存在");
        }

        return Result.success(newDishInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveCustomPlan(Long userId, CustomPlanSaveDTO dto) { // 🌟 接收上下文 userId
        // 1. 基础参数校验 (移除对 dto.getUserId() 的强依赖)
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return Result.error("方案名称不能为空");
        }
        if (dto.getBreakfastDishId() == null || dto.getLunchDishId() == null || dto.getDinnerDishId() == null) {
            return Result.error("必须完整配置一日三餐才能保存方案哦");
        }

        LocalDateTime now = LocalDateTime.now();

        // 2. 插入主表 (t_user_custom_plan)
        UserCustomPlan mainPlan = new UserCustomPlan();
        mainPlan.setUserId(userId); // 🌟 强行绑定当前登录人，杜绝通过篡改 ID 给别人塞方案
        mainPlan.setBasePlanId(dto.getBasePlanId());
        mainPlan.setName(dto.getName());
        mainPlan.setTotalCalorie(dto.getTotalCalorie());
        mainPlan.setCreateTime(now);

        customPlanMapper.insert(mainPlan);
        Long newCustomPlanId = mainPlan.getId();

        // 3. 循环插入三餐明细 (t_user_custom_plan_meal)
        saveMealDetail(newCustomPlanId, 1, dto.getBreakfastDishId(), now);
        saveMealDetail(newCustomPlanId, 2, dto.getLunchDishId(), now);
        saveMealDetail(newCustomPlanId, 3, dto.getDinnerDishId(), now);

        return Result.success("专属方案保存成功！可在【我的定制】中查看");
    }

    /**
     * 内部辅助方法：保存单餐明细
     */
    private void saveMealDetail(Long customPlanId, Integer mealType, Long dishId, LocalDateTime createTime) {
        UserCustomPlanMeal mealDetail = new UserCustomPlanMeal();
        mealDetail.setCustomPlanId(customPlanId);
        mealDetail.setMealType(mealType);
        mealDetail.setDishId(dishId);
        mealDetail.setCreateTime(createTime);
        customPlanMealMapper.insert(mealDetail);
    }
}