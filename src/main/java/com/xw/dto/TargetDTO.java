package com.xw.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设定用户目标 DTO
 * @author XW
 */
@Data
public class TargetDTO {
    // 必传参数
    private Long userId;

    // 目标体重 (kg)
    private BigDecimal targetWeight;

    // 目标达成日期 (如: 2026-12-31)
    private LocalDate targetDate;

    // 目标类型（减肥/健身）
    private String goalType;
}