package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * @author XW
 */
@Data
@Schema(description = "运动日历数据")
public class ExerciseCalendarDTO {
    @Schema(description = "记录日期", example = "2026-04-16")
    private LocalDate recordDate;

    @Schema(description = "运动项目名称", example = "跑步")
    private String exerciseName;

    @Schema(description = "运动时长(分钟)", example = "30")
    private Integer duration;

    @Schema(description = "消耗卡路里", example = "300")
    private Integer burnCalorie;
}
