package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "AI识别反馈纠正请求参数")
public class AiFeedbackDTO {
    @Schema(description = "识别记录ID（t_ai_recognize表主键）", required = true)
    private Long recordId;

    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "用户手动纠正的真实卡路里", example = "500")
    private Integer correctCalorie;
}
