// filepath: xw/vo/PlanVO.java
package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "计划VO")
public class PlanVO {
    @Schema(description = "计划ID")
    private Long id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "原理")
    private String principle;
    @Schema(description = "最小卡路里")
    private Integer calorieMin;
    @Schema(description = "最大卡路里")
    private Integer calorieMax;
    @Schema(description = "封面图片")
    private String coverImage;
    @Schema(description = "持续时间(天)")
    private Integer duration;
    @Schema(description = "减重范围")
    private String weightLoss;
    @Schema(description = "使用人数")
    private Integer usageCount;
    @Schema(description = "标签列表")
    private List<String> tagList;
    @Schema(description = "分类ID")
    private Long categoryId;
}