package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author XW
 */
@Data
@Schema(description = "用餐打卡请求参数")
public class MealCheckInDTO {

    @Schema(description = "普通菜品ID（系统菜品打卡时必传）", required = false)
    private Long dishId;

    @Schema(description = "AI识别记录ID（AI拍照打卡时必传）", required = false)
    private Long aiRecordId;

    @Schema(description = "食物名称（AI打卡建议传入，用于日记展示）", example = "清蒸鲈鱼")
    private String foodName;

    @Schema(description = "餐次类型：1-早餐 2-午餐 3-晚餐 4-加餐", required = true, example = "2")
    private Integer mealType;

    @Schema(description = "食物重量（克）", required = true, example = "200")
    private Integer weight;

    @Schema(description = "打卡日期", example = "2026-05-20")
    private LocalDate date;

    @Schema(description = "类型（默认传2）", example = "2")
    private Integer type;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "AI识别：直接热量值（每100g）")
    private Integer calorie;

    @Schema(description = "AI识别：碳水化合物(g/100g)")
    private BigDecimal carbohydrate;

    @Schema(description = "AI识别：蛋白质(g/100g)")
    private BigDecimal protein;

    @Schema(description = "AI识别：脂肪(g/100g)")
    private BigDecimal fat;
}