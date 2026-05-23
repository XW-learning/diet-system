package com.xw.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息DTO（前端上传用）
 * @author XW
 */
@Data
@Schema(description = "聊天消息请求参数")
public class ChatMessageDTO {
    @Schema(description = "消息角色：user/ai")
    private String role;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息创建时间")
    private LocalDateTime createTime;
}
