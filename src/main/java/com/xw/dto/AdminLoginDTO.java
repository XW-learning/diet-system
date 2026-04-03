package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "管理员登录请求参数")
public class AdminLoginDTO {
    @NotBlank(message = "账号不能为空")
    @Schema(description = "管理员账号", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "登录密码", example = "123456")
    private String password;
}