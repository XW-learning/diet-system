package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.Dish;
import com.xw.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author XW
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 联表查询：获取菜品的完整详情（含三大营养素）
     */
    @Select("""
        SELECT
            id, name, description, calorie, cook_method AS cookMethod,
            protein, fat, carbohydrate, fiber
        FROM t_dish
        WHERE id = #{dishId}
    """)
    DishVO getDishDetailWithNutrition(@Param("dishId") Long dishId);

    /**
     * 高级校验：检查某道菜的原材料是否与用户的过敏食材冲突
     * 联表路线：菜品配料表(drm) -> 原材料表(r) -> 用户过敏表(ua)
     */
    @Select("""
        SELECT r.name
        FROM t_dish_raw_material drm
        INNER JOIN t_raw_material r ON drm.material_id = r.id
        INNER JOIN t_user_allergy ua ON drm.material_id = ua.material_id
        WHERE drm.dish_id = #{dishId} AND ua.user_id = #{userId}
    """)
    List<String> checkAllergyConflict(@Param("userId") Long userId, @Param("dishId") Long dishId);
}