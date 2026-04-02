package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "打卡热量看板视图对象")
public class CheckInSummaryVO {
    @Schema(description = "还可以吃 (剩余额度) = 预算 - 饮食 + 运动")
    private Integer remainCalorie;

    @Schema(description = "今日预算热量")
    private Integer budgetCalorie;

    @Schema(description = "今日饮食摄入总热量")
    private Integer intakeCalorie;

    @Schema(description = "今日运动消耗总热量")
    private Integer burnCalorie;
}