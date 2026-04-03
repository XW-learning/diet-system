package com.xw.dto;

import lombok.Data;

/**
 * @author XW
 */
@Data
public class PlanFavoriteDTO {
    private Long userId;
    private Long planId;
    // 1-收藏，0-取消收藏
    private Integer action;
}