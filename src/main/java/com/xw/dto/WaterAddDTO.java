package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "饮水记录增加请求参数")
public class WaterAddDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "每次点击增加的饮水量(ml)", required = true, example = "200")
    private Integer addAmount;
}
