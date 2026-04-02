package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
public class DishReplaceDTO {
    @Schema(description = "当前用户ID（用于过敏原校验）", required = true)
    private Long userId;

    @Schema(description = "被替换的旧菜品ID")
    private Long oldDishId;

    @Schema(description = "想要替换成的新菜品ID", required = true)
    private Long newDishId;
}