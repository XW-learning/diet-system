package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户自定义方案餐次明细
 * @author XW
 */
@Data
@TableName("t_user_custom_plan_meal")
public class UserCustomPlanMeal {

    @TableId
    private Long id;

    /**
     * 自定义方案ID
     */
    private Long customPlanId;

    /**
     * 餐次：1早餐 2午餐 3晚餐
     */
    private Integer mealType;

    /**
     * 菜品ID (替换后的新菜品ID)
     */
    private Long dishId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}