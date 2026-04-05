package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

/**
 * 发布/修改动态 DTO
 * @author XW
 */
@Data
@Schema(description = "发布分享请求参数")
public class ShareSaveDTO {
    // 如果是修改，前端需要传 id；如果是新增，传 null
    private Long id;

    @Schema(description = "发布用户ID", required = true)
    private Long userId;

    @NotBlank(message = "分享内容不能为空")
    @Schema(description = "分享文字内容")
    private String content;

    @Schema(description = "可见性：0公开 1私密", defaultValue = "0")
    private Integer privacy;

    @Schema(description = "图片URL列表（最多9张）")
    private List<String> images;
}