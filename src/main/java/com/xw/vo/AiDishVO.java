package com.xw.vo;

import lombok.Data;

@Data
public class AiDishVO {
    private Long recordId;   // 识别记录ID(方便后续反馈纠错用)
    private String dishName; // AI 识别出的菜名
    private Integer calorie; // AI 估算的卡路里
}