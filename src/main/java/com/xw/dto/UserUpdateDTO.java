package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户基础信息更新 DTO
 * @author XW
 */
@Data
@Schema(description = "用户信息更新请求参数")
public class UserUpdateDTO {

    @Schema(description = "用户名", example = "张三")
    private String username;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "性别：0-女，1-男", example = "1")
    private Integer gender;

    @Schema(description = "年龄", example = "25")
    private Integer age;

    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "身高(cm)", example = "175.5")
    private BigDecimal height;
}
