package com.xw.dto;

import lombok.Data;

@Data
public class WaterAddDTO {
    private Long userId;
    /**
     * 每次点击增加的饮水量 (例如 200ml)
     */
    private Integer addAmount;
}