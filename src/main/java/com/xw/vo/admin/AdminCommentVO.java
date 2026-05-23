package com.xw.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "管理员-评论展示对象")
public class AdminCommentVO {
    @Schema(description = "评论ID")
    private Long id;
    @Schema(description = "关联动态ID")
    private Long shareId;
    @Schema(description = "评论人ID")
    private Long userId;
    @Schema(description = "评论人昵称")
    private String username;
    @Schema(description = "评论人头像")
    private String avatar;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "父评论ID")
    private Long parentId;
    @Schema(description = "被回复用户ID")
    private Long replyUserId;
    @Schema(description = "被回复用户昵称")
    private String replyUsername;
    @Schema(description = "评论时间")
    private LocalDateTime createTime;
}
