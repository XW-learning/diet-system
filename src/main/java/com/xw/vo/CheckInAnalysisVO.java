package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 打卡数据分析视图对象
 * @author XW
 */
@Data
@Schema(description = "打卡数据分析视图对象")
public class CheckInAnalysisVO {
    @Schema(description = "今日预算热量")
    private Integer budgetCalorie;

    @Schema(description = "今日饮食摄入总热量")
    private Integer intakeCalorie;

    @Schema(description = "今日运动消耗总热量")
    private Integer burnCalorie;

    @Schema(description = "剩余可吃热量（预算 - 饮食 + 运动）")
    private Integer remainCalorie;

    @Schema(description = "碳水化合物实际摄入量(g)")
    private BigDecimal totalCarbohydrate;

    @Schema(description = "蛋白质实际摄入量(g)")
    private BigDecimal totalProtein;

    @Schema(description = "脂肪实际摄入量(g)")
    private BigDecimal totalFat;

    @Schema(description = "碳水化合物推荐目标(g)")
    private BigDecimal recommendCarbohydrate;

    @Schema(description = "蛋白质推荐目标(g)")
    private BigDecimal recommendProtein;

    @Schema(description = "脂肪推荐目标(g)")
    private BigDecimal recommendFat;

    @Schema(description = "早餐热量")
    private Integer breakfastCalorie;

    @Schema(description = "午餐热量")
    private Integer lunchCalorie;

    @Schema(description = "晚餐热量")
    private Integer dinnerCalorie;

    @Schema(description = "加餐热量")
    private Integer snackCalorie;
}
