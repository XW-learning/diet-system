package com.xw.vo;

import com.xw.entity.CheckIn;
import com.xw.entity.ExerciseRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 某日打卡详情视图对象
 * @author XW
 */
@Data
@Schema(description = "打卡明细视图对象(含饮食与运动)")
public class CheckInDetailVO {

    @Schema(description = "打卡主表信息(当天的总热量总览)")
    private CheckIn checkIn;

    @Schema(description = "饮食明细列表(早/中/晚/加餐)")
    private List<MealVO> meals;

    @Schema(description = "运动明细列表")
    private List<ExerciseRecord> exercises;
}