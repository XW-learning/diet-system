package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.Dish;
import com.xw.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜品Mapper接口
 *
 * @author XW
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 获取菜品完整详情（含营养素信息）
     *
     * @param dishId 菜品ID
     * @return 菜品详情VO
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
     * 检查菜品原材料是否与用户过敏食材冲突
     *
     * @param userId 用户ID
     * @param dishId 菜品ID
     * @return 冲突的原材料名称列表
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