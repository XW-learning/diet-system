package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "管理员-社区动态展示对象")
public class AdminShareVO {
    @Schema(description = "动态ID")
    private Long id;
    @Schema(description = "发布者ID")
    private Long userId;
    @Schema(description = "发布者昵称")
    private String username;
    @Schema(description = "发布者头像")
    private String avatar;
    @Schema(description = "发布者手机号（脱敏展示用）")
    private String phone;
    @Schema(description = "分享内容")
    private String content;
    @Schema(description = "可见性：0公开 1私密")
    private Integer privacy;
    @Schema(description = "审核状态：0待审核 1通过 2驳回")
    private Integer auditStatus;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "评论数")
    private Integer commentCount;
    @Schema(description = "收藏数")
    private Integer collectionCount;
    @Schema(description = "转发数")
    private Integer shareCount;
    @Schema(description = "发布时间")
    private LocalDateTime createTime;
    @Schema(description = "图片URL列表")
    private List<String> images;
}
