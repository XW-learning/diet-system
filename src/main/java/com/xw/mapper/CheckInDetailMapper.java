package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.CheckInDetail;
import com.xw.vo.MealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author XW
 */
@Mapper
public interface CheckInDetailMapper extends BaseMapper<CheckInDetail> {

    /**
     * 联表查询：获取某次打卡下的所有菜品详细信息
     */
    @Select("""
        SELECT 
            cd.meal_type AS mealType,
            d.id AS dishId,
            d.name AS dishName,
            d.description,
            cd.calorie,
            d.cook_method AS cookMethod
        FROM t_check_in_detail cd
        INNER JOIN t_dish d ON cd.dish_id = d.id
        WHERE cd.check_in_id = #{checkInId}
        ORDER BY cd.meal_type ASC
    """)
    List<MealVO> getDetailMeals(@Param("checkInId") Long checkInId);
}