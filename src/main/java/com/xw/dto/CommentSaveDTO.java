package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "保存评论请求参数")
public class CommentSaveDTO {

    @Schema(description = "动态ID", required = true)
    private Long shareId;

    @Schema(description = "动态作者ID（用于发送通知）", required = true)
    private Long authorId;

    @Schema(description = "评论内容", required = true, example = "这个看起来很好吃！")
    private String content;

    @Schema(description = "父评论ID（回复评论时传入）")
    private Long parentId;

    @Schema(description = "被回复的用户ID")
    private Long replyUserId;
}
