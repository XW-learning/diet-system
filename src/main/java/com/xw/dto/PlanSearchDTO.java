package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "饮食方案搜索请求参数")
public class PlanSearchDTO {

    @Schema(description = "方案名称或原则关键词", example = "高蛋白")
    private String keyword;

    @Schema(description = "饮食目标（对应人群分类名称，如：减肥/健身）", example = "减肥")
    private String goal;

    @Schema(description = "最高卡路里限制", example = "1500")
    private Integer maxCalories;
}