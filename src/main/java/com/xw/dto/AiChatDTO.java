package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "AI聊天请求参数")
public class AiChatDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "用户发送的聊天内容", required = true, example = "今天吃什么好？")
    private String message;
}
