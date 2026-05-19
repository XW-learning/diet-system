package com.xw.dto;

import lombok.Data;
import java.util.List;

@Data
public class AdminPlanMealSaveDTO {
    private Long planId;
    private List<MealItem> meals;

    @Data
    public static class MealItem {
        private Integer mealType;
        private String mealName;
        private Integer sortOrder;
        private List<Long> dishIds;
    }
}
