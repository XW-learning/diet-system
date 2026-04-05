package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态展示视图对象
 * @author XW
 */
@Data
@Schema(description = "分享动态展示对象")
public class ShareVO {
    private Long id;
    private Long userId;

    // 🌟 联表带出的发布者信息，极大提升前端展示体验
    private String username;
    private String avatar;

    private String content;
    private Integer privacy;
    private Integer auditStatus; // 0待审核 1正常 2违规

    private Integer likeCount;
    private Integer commentCount;

    private LocalDateTime createTime;

    @Schema(description = "该动态包含的图片URL列表")
    private List<String> images;
}