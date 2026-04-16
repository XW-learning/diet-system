package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "重置密码请求参数")
public class ResetPasswordDTO {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "短信验证码", example = "123456")
    private String captcha;

    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码", example = "654321")
    private String newPassword;
}
