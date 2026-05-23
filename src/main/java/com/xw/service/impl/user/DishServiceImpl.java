package com.xw.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.dto.user.CustomPlanSaveDTO;
import com.xw.dto.user.DishReplaceDTO;
import com.xw.entity.user.Dish;
import com.xw.entity.user.UserCustomPlan;
import com.xw.entity.user.UserCustomPlanMeal;
import com.xw.exception.BusinessException;
import com.xw.mapper.user.DishMapper;
import com.xw.mapper.user.UserCustomPlanMapper;
import com.xw.mapper.user.UserCustomPlanMealMapper;
import com.xw.service.user.DishService;
import com.xw.vo.user.DishVO;
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
    public List<Dish> getDishList(String keyword) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Dish::getName, keyword.trim());
        }
        wrapper.orderByAsc(Dish::getCalorie);

        return dishMapper.selectList(wrapper);
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
    public DishVO replaceDish(Long userId, DishReplaceDTO dto) {
        if (dto.getNewDishId() == null) {
            throw new BusinessException("缺少必要参数");
        }

        List<String> conflictMaterials = dishMapper.checkAllergyConflict(userId, dto.getNewDishId());
        if (conflictMaterials != null && !conflictMaterials.isEmpty()) {
            String materials = String.join("、", conflictMaterials);
            throw new BusinessException("替换失败！该菜品含有您的过敏食材：" + materials + "，为了您的健康，请重新选择。");
        }

        DishVO newDishInfo = dishMapper.getDishDetailWithNutrition(dto.getNewDishId());

        if (newDishInfo == null) {
            throw new BusinessException("选择的菜品不存在");
        }

        return newDishInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveCustomPlan(Long userId, CustomPlanSaveDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException("方案名称不能为空");
        }
        if (dto.getBreakfastDishId() == null || dto.getLunchDishId() == null || dto.getDinnerDishId() == null) {
            throw new BusinessException("必须完整配置一日三餐才能保存方案哦");
        }

        LocalDateTime now = LocalDateTime.now();

        UserCustomPlan mainPlan = new UserCustomPlan();
        mainPlan.setUserId(userId);
        mainPlan.setBasePlanId(dto.getBasePlanId());
        mainPlan.setName(dto.getName());
        mainPlan.setTotalCalorie(dto.getTotalCalorie());
        mainPlan.setCreateTime(now);

        customPlanMapper.insert(mainPlan);
        Long newCustomPlanId = mainPlan.getId();

        saveMealDetail(newCustomPlanId, 1, dto.getBreakfastDishId(), now);
        saveMealDetail(newCustomPlanId, 2, dto.getLunchDishId(), now);
        saveMealDetail(newCustomPlanId, 3, dto.getDinnerDishId(), now);

        return "专属方案保存成功！可在【我的定制】中查看";
    }

    private void saveMealDetail(Long customPlanId, Integer mealType, Long dishId, LocalDateTime createTime) {
        UserCustomPlanMeal mealDetail = new UserCustomPlanMeal();
        mealDetail.setCustomPlanId(customPlanId);
        mealDetail.setMealType(mealType);
        mealDetail.setDishId(dishId);
        mealDetail.setCreateTime(createTime);
        customPlanMealMapper.insert(mealDetail);
    }
}
