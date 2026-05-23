package com.xw.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "日运动分析视图对象")
public class DailyExerciseVO {

    @Schema(description = "分析日期")
    private LocalDate date;

    @Schema(description = "总运动时长(分钟)")
    private Integer totalDuration;

    @Schema(description = "总消耗热量(千卡)")
    private Integer totalBurnCalorie;

    @Schema(description = "运动次数")
    private Integer exerciseCount;

    @Schema(description = "运动明细列表")
    private List<ExerciseRecordItem> records;

    @Data
    @Schema(description = "运动记录项")
    public static class ExerciseRecordItem {

        @Schema(description = "运动项目名称")
        private String exerciseName;

        @Schema(description = "运动时长(分钟)")
        private Integer duration;

        @Schema(description = "消耗热量(千卡)")
        private Integer burnCalorie;
    }
}
