// filepath: xw/vo/MealVO.java
package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * @author XW
 */
@Data
@Schema(description = "Meal VO")
public class MealVO {
    @Schema(description = "Day Number")
    private Integer dayNumber;
    @Schema(description = "Meal Type")
    private Integer mealType;
    @Schema(description = "Dish ID")
    private Long dishId;
    @Schema(description = "Dish Name")
    private String dishName;
    @Schema(description = "Description")
    private String description;
    @Schema(description = "Calorie")
    private Integer calorie;
    @Schema(description = "Cook Method")
    private String cookMethod;
    private String dishImage;
    private BigDecimal weight;
    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
}