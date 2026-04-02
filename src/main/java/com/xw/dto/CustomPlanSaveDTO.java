package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "保存自定义方案请求参数")
public class CustomPlanSaveDTO {

    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "基础方案ID（从哪个模板修改来的，可为空）")
    private Long basePlanId;

    @Schema(description = "自定义方案名称", required = true, example = "我的周三去水肿餐")
    private String name;

    @Schema(description = "替换后的总预估卡路里", required = true)
    private Integer totalCalorie;

    @Schema(description = "早餐菜品ID", required = true)
    private Long breakfastDishId;

    @Schema(description = "午餐菜品ID", required = true)
    private Long lunchDishId;

    @Schema(description = "晚餐菜品ID", required = true)
    private Long dinnerDishId;
}