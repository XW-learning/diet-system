package com.xw.dto;

import lombok.Data;

@Data
public class InteractDTO {
    private Long userId;      // 操作人(当前登录用户)
    private Long shareId;     // 被操作的动态ID
    private Long authorId;    // 动态作者的ID (传过来是为了发通知用，省去后端再查一次)
}