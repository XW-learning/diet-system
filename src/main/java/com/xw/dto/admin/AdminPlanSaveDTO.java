package com.xw.dto.admin;

import lombok.Data;

@Data
public class AdminPlanSaveDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private Integer calorieMin;
    private Integer calorieMax;
    private String principle;
}
