package com.xw.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("t_plan_meal_dish")
public class PlanMealDish {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long mealGroupId;
    private Long dishId;
    private BigDecimal weight;
}
