package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "管理员-餐次分组（含菜品列表）")
public class AdminMealGroupVO {
    @Schema(description = "餐次分组ID")
    private Long id;
    @Schema(description = "餐次类型：1早餐 2午餐 3晚餐 4加餐")
    private Integer mealType;
    @Schema(description = "餐次名称，如活力早餐")
    private String mealName;
    @Schema(description = "排序")
    private Integer sortOrder;
    @Schema(description = "该餐次包含的菜品列表")
    private List<AdminDishItemVO> dishes;
}
