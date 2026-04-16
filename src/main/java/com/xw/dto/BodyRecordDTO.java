package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 录入身材记录 DTO
 * @author XW
 */
@Data
@Schema(description = "身体数据记录请求参数")
public class BodyRecordDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "身高(cm)", required = true, example = "175.5")
    private BigDecimal height;

    @Schema(description = "体重(kg)", required = true, example = "65.0")
    private BigDecimal weight;

    @Schema(description = "腰围(cm)", example = "80.0")
    private BigDecimal waist;

    @Schema(description = "臀围(cm)", example = "95.0")
    private BigDecimal hip;

    @Schema(description = "胸围(cm)", example = "90.0")
    private BigDecimal chest;
}
