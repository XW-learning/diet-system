package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 菜品详情视图对象（包含营养成分）
 * @author XW
 */
@Data
@Schema(description = "菜品详情及营养成分")
public class DishVO {
    private Long id;
    private String name;
    private String description;
    private Integer calorie;
    private String cookMethod;

    // 以下来自 t_dish_nutrition 表
    @Schema(description = "蛋白质(g)")
    private BigDecimal protein;
    @Schema(description = "脂肪(g)")
    private BigDecimal fat;
    @Schema(description = "碳水化合物(g)")
    private BigDecimal carbohydrate;
    @Schema(description = "膳食纤维(g)")
    private BigDecimal fiber;
}