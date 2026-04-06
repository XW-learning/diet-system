package com.xw.dto;

import lombok.Data;

@Data
public class AiRecognizeDTO {
    private Long userId;
    private String imageUrl; // 前端传来的饭菜图片 URL 或 Base64
}