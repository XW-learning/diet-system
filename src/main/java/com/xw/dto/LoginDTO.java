package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "用户登录请求参数")
public class LoginDTO {
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录密码", example = "123456")
    private String password;
}
