package com.xw.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 录入身材记录 DTO
 * @author XW
 */
@Data
public class BodyRecordDTO {
    private Long userId;

    // 必须传的两个核心指标
    private BigDecimal height; // 身高 (cm)
    private BigDecimal weight; // 体重 (kg)

    // 可选指标
    private BigDecimal waist;  // 腰围 (cm)
    private BigDecimal hip;    // 臀围 (cm)
}