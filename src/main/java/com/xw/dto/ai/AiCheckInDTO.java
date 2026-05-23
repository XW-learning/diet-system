package com.xw.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * AI 识别结果打卡请求参数
 * @author XW
 */
@Data
@Schema(description = "AI 识别结果打卡请求参数")
public class AiCheckInDTO {

    @Schema(description = "AI 识别记录ID（t_ai_recognize 主键）", required = true)
    private Long recordId;

    @Schema(description = "餐次类型：1-早餐 2-午餐 3-晚餐 4-加餐", required = true, example = "2")
    private Integer mealType;

    @Schema(description = "食物克数(g)", required = true, example = "300")
    private Integer weight;

    @Schema(description = "打卡日期", example = "2026-05-19")
    private LocalDate date;

    @Schema(description = "AI估算的卡路里（每100g），前端优先传递")
    private Integer calorie;

    @Schema(description = "蛋白质(g/100g)")
    private BigDecimal protein;

    @Schema(description = "脂肪(g/100g)")
    private BigDecimal fat;

    @Schema(description = "碳水化合物(g/100g)")
    private BigDecimal carbohydrate;

    @Schema(description = "膳食纤维(g/100g)")
    private BigDecimal fiber;
}
