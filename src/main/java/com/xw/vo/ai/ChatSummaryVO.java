package com.xw.vo.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天压缩摘要视图对象
 * @author XW
 */
@Data
@Schema(description = "聊天压缩摘要")
public class ChatSummaryVO {
    @Schema(description = "摘要ID")
    private Long id;

    @Schema(description = "摘要内容")
    private String summary;

    @Schema(description = "覆盖消息数")
    private Integer messageCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
