package com.xw.dto.admin;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdminDishSaveDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private String description;
    private Integer calorie;
    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal fiber;
    private BigDecimal refWeight;
    private String weightUnit;
    private String cookMethod;
    private String imageUrl;
}
