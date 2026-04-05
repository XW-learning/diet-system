package com.xw.dto;

import lombok.Data;

@Data
public class CommentSaveDTO {
    private Long userId;      // 评论人(当前用户)
    private Long shareId;     // 动态ID
    private Long authorId;    // 动态作者ID (用来发通知)
    private String content;   // 评论内容

    // 如果是回复别人的评论，需要传下面两个字段；如果是直接评论动态，传 null
    private Long parentId;    // 盖楼的父评论ID
    private Long replyUserId; // 被回复的那个人的ID
}