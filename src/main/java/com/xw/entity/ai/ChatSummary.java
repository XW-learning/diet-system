package com.xw.entity.ai;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天压缩摘要实体
 * @author XW
 */
@Data
@TableName("t_chat_summary")
public class ChatSummary {
    @TableId
    private Long id;
    private Long userId;
    private String summary;
    private Integer messageCount;
    private Long startMsgId;
    private Long endMsgId;
    private LocalDateTime createTime;
}
