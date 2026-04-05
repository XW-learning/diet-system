package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_comment")
public class Comment {
    @TableId
    private Long id;
    private Long shareId;
    private Long userId; // 评论人的ID
    private String content; // 评论内容
    private Long parentId; // 父评论ID（一级评论填 0 或 null）
    private Long replyUserId; // 被回复人的ID（一级评论填 null）
    private LocalDateTime createTime;
}