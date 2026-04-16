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
    @Schema(description = "菜品ID")
    private Long id;

    @Schema(description = "菜品名称")
    private String name;

    @Schema(description = "菜品描述")
    private String description;

    @Schema(description = "卡路里(千卡)")
    private Integer calorie;

    @Schema(description = "烹饪方法")
    private String cookMethod;

    @Schema(description = "蛋白质(g)")
    private BigDecimal protein;

    @Schema(description = "脂肪(g)")
    private BigDecimal fat;

    @Schema(description = "碳水化合物(g)")
    private BigDecimal carbohydrate;

    @Schema(description = "膳食纤维(g)")
    private BigDecimal fiber;
}
