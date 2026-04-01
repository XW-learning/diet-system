package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.Plan;
import java.util.List;

import com.xw.vo.MealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author XW
 */
@Mapper
public interface PlanMapper extends BaseMapper<Plan> {
    /**
     * 联表查询：获取用户的方案收藏列表
     * 逻辑：通过收藏表 (t_collection) 的 target_id 找到方案表 (t_diet_plan) 的完整数据
     * 注意：type = 2 代表收藏的是方案
     */
    @Select("""
        SELECT p.* FROM t_diet_plan p 
        INNER JOIN t_collection c ON p.id = c.target_id 
        WHERE c.user_id = #{userId} AND c.type = 2 
        ORDER BY c.create_time DESC
    """)
    List<Plan> getUserFavoritePlans(@Param("userId") Long userId);

    /**
     * 联表查询：获取某个方案下包含的所有菜品详情
     * 关联路线：t_diet_plan_meal (中间表) -> t_dish (菜品表)
     */
    @Select("""
        SELECT 
            m.meal_type AS mealType,
            d.id AS dishId,
            d.name AS dishName,
            d.description,
            d.calorie,
            d.cook_method AS cookMethod
        FROM t_diet_plan_meal m
        INNER JOIN t_dish d ON m.dish_id = d.id
        WHERE m.plan_id = #{planId}
        ORDER BY m.meal_type ASC
    """)
    List<MealVO> getPlanMeals(@Param("planId") Long planId);
}