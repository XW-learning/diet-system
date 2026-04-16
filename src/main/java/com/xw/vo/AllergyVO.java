package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 过敏食材视图对象
 * @author XW
 */
@Data
@Schema(description = "过敏食材视图对象")
public class AllergyVO {
    @Schema(description = "食材ID（前端调用删除接口时需要传此ID）")
    private Long materialId;

    @Schema(description = "食材名称（如：花生、牛奶，用于前端展示）")
    private String name;
}
