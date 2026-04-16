package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "动态互动请求参数（点赞/取消点赞）")
public class InteractDTO {
    @Schema(description = "操作人用户ID", required = true)
    private Long userId;

    @Schema(description = "被操作的动态ID", required = true)
    private Long shareId;

    @Schema(description = "动态作者ID（用于发送通知）", required = true)
    private Long authorId;
}
