package com.xw.vo.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author XW
 */
@Data
@Schema(description = "聊天历史完整响应")
public class ChatHistoryFullVO {
    @Schema(description = "压缩摘要列表")
    private List<ChatSummaryVO> summaries;

    @Schema(description = "最近消息列表")
    private List<ChatHistoryVO> messages;
}
