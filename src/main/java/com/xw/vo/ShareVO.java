package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态展示视图对象
 *
 * @author XW
 */
@Data
@Schema(description = "分享动态展示对象")
public class ShareVO {
    @Schema(description = "动态ID")
    private Long id;

    @Schema(description = "发布者ID")
    private Long userId;

    @Schema(description = "发布者昵称")
    private String username;

    @Schema(description = "发布者头像")
    private String avatar;

    @Schema(description = "分享文字内容")
    private String content;

    @Schema(description = "可见性：0公开 1私密")
    private Integer privacy;

    @Schema(description = "审核状态 0待审核 1正常 2违规")
    private Integer auditStatus;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "收藏数")
    private Integer collectionCount;

    @Schema(description = "转发分享数")
    private Integer shareCount;

    @Schema(description = "发布时间")
    private LocalDateTime createTime;

    @Schema(description = "该动态包含的图片URL列表")
    private List<String> images;
}