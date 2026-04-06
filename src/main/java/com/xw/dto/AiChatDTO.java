package com.xw.dto;

import lombok.Data;

/**
 * @author XW
 */
@Data
public class AiChatDTO {
    private Long userId;     // 谁在提问
    private String message;  // 用户发送的聊天内容
}