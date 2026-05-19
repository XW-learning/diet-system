package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "管理员-食谱列表项")
public class AdminPlanVO {
    @Schema(description = "食谱ID")
    private Long id;
    @Schema(description = "食谱名称")
    private String name;
    @Schema(description = "分类ID")
    private Long categoryId;
    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "最低卡路里")
    private Integer calorieMin;
    @Schema(description = "最高卡路里")
    private Integer calorieMax;
    @Schema(description = "饮食原则")
    private String principle;
    @Schema(description = "状态：1启用 0禁用")
    private Integer status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
