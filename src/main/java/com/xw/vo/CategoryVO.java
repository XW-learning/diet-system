package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分类信息视图对象
 * @author XW
 */
@Data
@Schema(description = "分类信息视图对象")
public class CategoryVO {
    @Schema(description = "分类ID")
    private Integer id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "排序权重")
    private Integer sortOrder;
}
