// filepath: xw/mapper/PlanMapper.java
package com.xw.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.user.Plan;
import java.util.List;
import com.xw.vo.user.MealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PlanMapper extends BaseMapper<Plan> {
    @Select("""
        SELECT p.* FROM t_diet_plan p 
        INNER JOIN t_collection c ON p.id = c.target_id 
        WHERE c.user_id = #{userId} AND c.type = 2 
        ORDER BY c.create_time DESC
    """)
    List<Plan> getUserFavoritePlans(@Param("userId") Long userId);

    @Select("""
        SELECT 
            m.day_number AS dayNumber,
            m.meal_type AS mealType,
            m.weight AS weight,
            d.id AS dishId,
            d.name AS dishName,
            d.description,
            d.calorie,
            d.image_url AS dishImage,
            d.carbohydrate,
            d.protein,
            d.fat,
            d.cook_method AS cookMethod
        FROM t_diet_plan_meal m
        INNER JOIN t_dish d ON m.dish_id = d.id
        WHERE m.plan_id = #{planId}
        ORDER BY m.day_number ASC, m.meal_type ASC
    """)
    List<MealVO> getPlanMeals(@Param("planId") Long planId);
}