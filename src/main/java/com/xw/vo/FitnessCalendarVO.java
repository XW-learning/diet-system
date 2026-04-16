package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 健身日历视图对象
 * @author XW
 */
@Data
@Schema(description = "健身日历视图对象")
public class FitnessCalendarVO {
    @Schema(description = "连续打卡天数")
    private Integer continuousDays;

    @Schema(description = "本月累计运动天数")
    private Integer totalWorkoutDays;

    @Schema(description = "本月累计运动时长(分钟)")
    private Integer totalDuration;

    @Schema(description = "本月累计消耗热量(千卡)")
    private Integer totalBurnCalorie;

    @Schema(description = "Top5运动排行榜")
    private List<WorkoutRankVO> top5Workouts;

    @Schema(description = "日历网格数据（Key为日期格式：2026-04-16）")
    private Map<String, DailyFitnessVO> dailyData;

    /**
     * 运动排行榜项
     */
    @Data
    @Schema(description = "运动排行榜项")
    public static class WorkoutRankVO {
        @Schema(description = "运动名称")
        private String name;

        @Schema(description = "运动时长(分钟)")
        private Integer duration;
    }

    /**
     * 每日健身数据
     */
    @Data
    @Schema(description = "每日健身数据")
    public static class DailyFitnessVO {
        @Schema(description = "运动时长(分钟)")
        private Integer duration;

        @Schema(description = "消耗热量(千卡)")
        private Integer calories;
    }
}
