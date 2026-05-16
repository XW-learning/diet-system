package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天历史视图对象
 * @author XW
 */
@Data
@Schema(description = "聊天历史返回")
public class ChatHistoryVO {
    @Schema(description = "消息角色")
    private String role;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
