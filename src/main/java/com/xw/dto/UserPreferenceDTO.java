package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存饮食偏好 DTO
 * @author XW
 */
@Data
@Schema(description = "用户饮食偏好请求参数")
public class UserPreferenceDTO {

    @Schema(description = "口味偏好", example = "少油少盐")
    private String taste;

    @Schema(description = "饮食类型", example = "低碳水饮食")
    private String dietType;
}
