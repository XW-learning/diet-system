package com.xw.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "周运动分析视图对象")
public class WeeklyExerciseVO {

    @Schema(description = "周开始日期(周一)")
    private LocalDate weekStart;

    @Schema(description = "周结束日期(周日)")
    private LocalDate weekEnd;

    @Schema(description = "总运动时长(分钟)")
    private Integer totalDuration;

    @Schema(description = "总消耗热量(千卡)")
    private Integer totalBurnCalorie;

    @Schema(description = "运动天数")
    private Integer totalWorkoutDays;

    @Schema(description = "日均运动时长(分钟)")
    private Double avgDailyDuration;

    @Schema(description = "日均消耗热量(千卡)")
    private Double avgDailyCalorie;

    @Schema(description = "每日明细(7天，无运动的天数为0)")
    private List<DailyBreakdown> dailyBreakdown;

    @Schema(description = "本周运动排行")
    private List<TopExerciseItem> topExercises;

    @Data
    @Schema(description = "每日分解数据")
    public static class DailyBreakdown {

        @Schema(description = "日期")
        private LocalDate date;

        @Schema(description = "星期几(1=周一 .. 7=周日)")
        private Integer dayOfWeek;

        @Schema(description = "运动时长(分钟)")
        private Integer duration;

        @Schema(description = "消耗热量(千卡)")
        private Integer burnCalorie;
    }

    @Data
    @Schema(description = "运动排行项")
    public static class TopExerciseItem {

        @Schema(description = "运动项目名称")
        private String exerciseName;

        @Schema(description = "运动次数")
        private Integer count;

        @Schema(description = "总时长(分钟)")
        private Integer totalDuration;

        @Schema(description = "总消耗(千卡)")
        private Integer totalBurnCalorie;
    }
}
