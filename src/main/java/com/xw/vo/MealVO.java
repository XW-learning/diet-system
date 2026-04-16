package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 方案详情中的单道菜品视图对象
 * @author XW
 */
@Data
@Schema(description = "餐次菜品详情视图对象")
public class MealVO {
    @Schema(description = "餐次类型：1-早餐 2-午餐 3-晚餐 4-加餐")
    private Integer mealType;

    @Schema(description = "菜品ID")
    private Long dishId;

    @Schema(description = "菜品名称")
    private String dishName;

    @Schema(description = "菜品描述")
    private String description;

    @Schema(description = "卡路里(千卡)")
    private Integer calorie;

    @Schema(description = "烹饪方法")
    private String cookMethod;
}
