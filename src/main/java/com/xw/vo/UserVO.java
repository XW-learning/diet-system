package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户完整信息视图对象
 * @author XW
 */
@Data
@Schema(description = "用户完整信息视图对象")
public class UserVO {
    @Schema(description = "用户ID")
    private String id;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别：0-女 1-男")
    private Integer gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "人群分类名称（如：减肥人群）")
    private String categoryName;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "BMI指数")
    private BigDecimal bmi;

    @Schema(description = "腰围(cm)")
    private BigDecimal waist;

    @Schema(description = "臀围(cm)")
    private BigDecimal hip;

    @Schema(description = "目标体重(kg)")
    private BigDecimal targetWeight;

    @Schema(description = "目标达成日期")
    private LocalDate targetDate;

    @Schema(description = "目标类型：减肥/健身")
    private String goalType;

    @Schema(description = "口味偏好")
    private String taste;

    @Schema(description = "饮食类型")
    private String dietType;
}
