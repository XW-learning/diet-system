package com.xw.vo;

import lombok.Data;

/**
 * 方案详情中的单道菜品视图对象
 * @author XW
 */
@Data
public class MealVO {
    // 餐次类型：1早餐 2午餐 3晚餐
    private Integer mealType;

    // 菜品详情
    private Long dishId;
    private String dishName;
    private String description;
    private Integer calorie;
    private String cookMethod; // 制作方法
}