package com.xw.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MealCheckInDTO {
    private Long userId;     // 用户ID
    private Long dishId;     // 菜品ID
    private Integer mealType; // 餐次类型 (1-早, 2-中, 3-晚)
    private Integer type;     // 记录类型 (1-方案, 2-自定义)
    private String remark;    // 备注
    private LocalDate date;   // 打卡日期
    private Integer weight;   // 🌟 新增：用户输入的克数
}