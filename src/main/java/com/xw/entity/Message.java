package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息通知实体类 (对应 t_message 表)
 * @author XW
 */
@Data
@TableName("t_message")
public class Message {

    // 消息主键ID
    @TableId
    private Long id;

    // 接收者的用户ID
    private Long receiverId;

    // 发送者的用户ID (如果是AI审核系统发的通知，这里可以是 0)
    private Long senderId;

    /**
     * 消息类型字典：
     * 1 = 点赞 (Like)
     * 2 = 评论 (Comment)
     * 3 = 收藏 (Collect)
     * 4 = 系统通知/AI审核反馈 (System Notice)
     */
    private Integer type;

    // 业务关联ID (前端点击这条消息时，用来跳转到那篇分享动态的详情页)
    private Long sourceId;

    // 消息内容 (比如别人评论了什么文字内容，或者AI驳回的具体原因)
    private String content;

    // 是否已读：0未读，1已读
    private Integer isRead;

    // 消息产生时间
    private LocalDateTime createTime;
}