package com.xw.vo;

import com.xw.entity.Plan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 饮食方案完整详情视图对象
 * @author XW
 */
@Data
@Schema(description = "饮食方案完整详情视图对象")
public class PlanDetailVO {
    @Schema(description = "方案基础信息（名称、卡路里等）")
    private Plan plan;

    @Schema(description = "该方案下包含的所有菜品（按早中晚餐排好序）")
    private List<MealVO> meals;
}
