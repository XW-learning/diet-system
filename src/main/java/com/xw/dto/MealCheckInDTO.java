package com.xw.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

/**
 * @author XW
 */
@Data
@Schema(description = "饮食打卡请求参数")
public class MealCheckInDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "餐次：1早 2午 3晚 4加餐", required = true)
    private Integer mealType;

    @Schema(description = "菜品ID", required = true)
    private Long dishId;

    @Schema(description = "类型：1方案 2自定义", defaultValue = "2")
    private Integer type;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "打卡日期(yyyy-MM-dd)，不传默认今天")
    private LocalDate date;

    @Schema(description = "打卡备注")
    private String remark;
}