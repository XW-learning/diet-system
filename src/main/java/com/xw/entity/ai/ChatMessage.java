package com.xw.entity.ai;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息实体
 * @author XW
 */
@Data
@TableName("t_chat_message")
public class ChatMessage {
    @TableId
    private Long id;
    private Long userId;
    private String role;
    private String content;
    private LocalDateTime createTime;
}
