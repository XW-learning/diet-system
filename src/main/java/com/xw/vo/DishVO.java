// filepath: xw/vo/DishVO.java
package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Dish VO")
public class DishVO {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "Name")
    private String name;
    @Schema(description = "Description")
    private String description;
    @Schema(description = "Calorie")
    private Integer calorie;
    @Schema(description = "Cook Method")
    private String cookMethod;
    @Schema(description = "Protein (g)")
    private BigDecimal protein;
    @Schema(description = "Fat (g)")
    private BigDecimal fat;
    @Schema(description = "Carbohydrate (g)")
    private BigDecimal carbohydrate;
    @Schema(description = "Fiber (g)")
    private BigDecimal fiber;
    private BigDecimal refWeight;
    private String weightUnit;
    private String imageUrl;
}