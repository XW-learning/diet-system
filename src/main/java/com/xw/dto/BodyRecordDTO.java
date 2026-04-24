package com.xw.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime; // 🌟 记得加上这个导入

/**
 * 录入身材/经期记录 DTO
 * @author XW
 */
@Data
@Schema(description = "身体数据记录请求参数")
public class BodyRecordDTO {

    @Schema(description = "身高(cm)", required = false, example = "175.5")
    private BigDecimal height;

    @Schema(description = "体重(kg)", required = false, example = "65.0")
    private BigDecimal weight;

    @Schema(description = "腰围(cm)", example = "80.0")
    private BigDecimal waist;

    @Schema(description = "臀围(cm)", example = "95.0")
    private BigDecimal hip;

    @Schema(description = "胸围(cm)", example = "90.0")
    private BigDecimal chest;

    // 🌟 新增：经期开始和结束日期
    @Schema(description = "经期开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate periodStartDate;

    @Schema(description = "经期结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate periodEndDate;

    // 🌟 新增：用于补打卡的特定日期时间
    @Schema(description = "打卡指定的记录时间（不传则默认当前时间）", example = "2026-04-16 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime recordTime;

    // 🌟 新增标记：是否是经期弹窗发起的专门更新
    @Schema(description = "是否是专门更新经期(允许清空)")
    private Boolean isPeriodUpdate;
}