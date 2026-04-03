package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "AI菜品识别结果视图对象")
public class AiDishVO {
    private String dishName;
    private Integer calorie;
    private Double protein;
    private Double fat;
    private Double carbohydrate;
}