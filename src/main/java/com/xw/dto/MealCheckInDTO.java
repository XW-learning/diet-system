package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * @author XW
 */
@Data
@Schema(description = "用餐打卡请求参数")
public class MealCheckInDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "菜品ID", required = true)
    private Long dishId;

    @Schema(description = "餐次类型：1-早餐 2-午餐 3-晚餐", required = true, example = "2")
    private Integer mealType;

    @Schema(description = "记录类型：1-方案 2-自定义", required = true, example = "1")
    private Integer type;

    @Schema(description = "备注", example = "今天吃得有点多")
    private String remark;

    @Schema(description = "打卡日期", example = "2026-04-16")
    private LocalDate date;

    @Schema(description = "食物克数(g)", example = "300")
    private Integer weight;
}
