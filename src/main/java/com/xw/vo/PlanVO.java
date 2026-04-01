package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 饮食方案搜索结果视图对象
 * @author XW
 */
@Data
@Schema(description = "饮食方案展示对象")
public class PlanVO {

    @Schema(description = "方案ID")
    private Long id;

    @Schema(description = "方案名称")
    private String name;

    @Schema(description = "饮食原则说明")
    private String principle;

    @Schema(description = "最低卡路里")
    private Integer calorieMin;

    @Schema(description = "最高卡路里")
    private Integer calorieMax;
}