package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "餐次中的菜品项")
public class AdminDishItemVO {
    @Schema(description = "菜品ID")
    private Long id;
    @Schema(description = "菜品名称")
    private String name;
    @Schema(description = "热量(kcal)")
    private Integer calorie;
    @Schema(description = "份量")
    private BigDecimal weight;
    @Schema(description = "单位")
    private String weightUnit;
    @Schema(description = "蛋白质(g)")
    private BigDecimal protein;
    @Schema(description = "脂肪(g)")
    private BigDecimal fat;
    @Schema(description = "碳水(g)")
    private BigDecimal carbohydrate;
    @Schema(description = "图片URL")
    private String imageUrl;
}
