package com.xw.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CheckInAnalysisVO {
    // 1. 基础热量圈数据
    private Integer budgetCalorie; // 预算
    private Integer intakeCalorie; // 摄入
    private Integer burnCalorie;   // 消耗(运动)
    private Integer remainCalorie; // 剩余

    // 2. 三大营养素 - 实际摄入
    private BigDecimal totalCarbohydrate;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;

    // 3. 三大营养素 - 推荐目标 (后端直接算好给前端)
    private BigDecimal recommendCarbohydrate;
    private BigDecimal recommendProtein;
    private BigDecimal recommendFat;

    // 4. 各餐次热量分布
    private Integer breakfastCalorie; // 早餐
    private Integer lunchCalorie;     // 午餐
    private Integer dinnerCalorie;    // 晚餐
    private Integer snackCalorie;     // 加餐
}