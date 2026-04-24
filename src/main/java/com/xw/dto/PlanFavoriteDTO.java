package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "饮食方案收藏请求参数")
public class PlanFavoriteDTO {

    @Schema(description = "方案ID", required = true)
    private Long planId;

    @Schema(description = "操作类型：1-收藏 0-取消收藏", required = true, example = "1")
    private Integer action;
}
