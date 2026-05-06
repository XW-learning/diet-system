package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "设置为我的食谱计划请求参数")
public class PlanActivateDTO {

    @Schema(description = "方案ID", required = true)
    private Long planId;
}
