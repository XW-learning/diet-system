package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 设定用户目标 DTO
 * @author XW
 */
@Data
@Schema(description = "用户目标设定请求参数")
public class TargetDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "目标体重(kg)", example = "60.0")
    private BigDecimal targetWeight;

    @Schema(description = "目标达成日期", example = "2026-12-31")
    private LocalDate targetDate;

    @Schema(description = "目标类型：减肥/健身", example = "减肥")
    private String goalType;
}
