package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI识别菜品结果视图对象
 * @author XW
 */
@Data
@Schema(description = "AI识别菜品结果视图对象")
public class AiDishVO {
    @Schema(description = "识别记录ID（用于后续反馈纠错）")
    private Long recordId;

    @Schema(description = "AI识别出的菜名")
    private String dishName;

    @Schema(description = "AI估算的卡路里")
    private Integer calorie;
}
