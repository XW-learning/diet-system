package com.xw.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "管理员-食谱详情（含餐次和菜品）")
public class AdminPlanDetailVO {
    @Schema(description = "食谱基本信息")
    private AdminPlanVO plan;
    @Schema(description = "餐次分组列表")
    private List<AdminMealGroupVO> meals;
}
