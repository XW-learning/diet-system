package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "AI图像识别请求参数")
public class AiRecognizeDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "饭菜图片URL或Base64编码", required = true)
    private String imageUrl;
}
