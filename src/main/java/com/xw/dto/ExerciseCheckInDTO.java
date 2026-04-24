package com.xw.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * @author XW
 */
@Data
@Schema(description = "运动打卡请求参数")
public class ExerciseCheckInDTO {

    @Schema(description = "运动项目ID (关联 t_exercise 表)", required = true)
    private Long exerciseId;

    @Schema(description = "运动时长(分钟)", required = true)
    private Integer duration;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "打卡日期(yyyy-MM-dd)，不传默认今天")
    private LocalDate date;
}