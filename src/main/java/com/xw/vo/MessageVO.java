package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息列表展示视图对象
 * @author XW
 */
@Data
@Schema(description = "消息通知展示对象")
public class MessageVO {

    private Long id;
    private Long receiverId;
    private Long senderId;

    // 🌟 数据聚合：发送者的昵称和头像
    @Schema(description = "发送者昵称 (系统通知时为 '系统管理员')")
    private String senderName;
    @Schema(description = "发送者头像")
    private String senderAvatar;

    @Schema(description = "消息类型：1点赞 2评论 3收藏 4系统通知")
    private Integer type;

    @Schema(description = "关联业务ID(如分享动态的ID)")
    private Long sourceId;

    @Schema(description = "消息具体内容")
    private String content;

    @Schema(description = "阅读状态：0未读 1已读")
    private Integer isRead;

    private LocalDateTime createTime;
}