package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "菜品分类请求参数")
public class CategoryDTO {
    @Schema(description = "分类ID")
    private Integer id;

    @Schema(description = "分类名称", example = "川菜")
    private String name;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;
}
