package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "管理员-菜品展示对象")
public class AdminDishVO {
    @Schema(description = "菜品ID")
    private Long id;
    @Schema(description = "菜品名称")
    private String name;
    @Schema(description = "分类ID")
    private Long categoryId;
    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "热量(kcal)")
    private Integer calorie;
    @Schema(description = "碳水(g)")
    private BigDecimal carbohydrate;
    @Schema(description = "蛋白质(g)")
    private BigDecimal protein;
    @Schema(description = "脂肪(g)")
    private BigDecimal fat;
    @Schema(description = "纤维(g)")
    private BigDecimal fiber;
    @Schema(description = "参考重量")
    private BigDecimal refWeight;
    @Schema(description = "重量单位")
    private String weightUnit;
    @Schema(description = "烹饪方式")
    private String cookMethod;
    @Schema(description = "图片URL")
    private String imageUrl;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
