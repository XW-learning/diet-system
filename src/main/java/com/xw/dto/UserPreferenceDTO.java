package com.xw.dto;

import lombok.Data;

/**
 * 保存饮食偏好 DTO
 * @author XW
 */
@Data
public class UserPreferenceDTO {
    // 必传参数：当前用户的 ID
    private Long userId;

    // 口味偏好（例如：少油少盐、无忌口等）
    private String taste;

    // 饮食类型（例如：低碳水饮食、高蛋白饮食等）
    private String dietType;
}