package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author XW
 */
@Data
@Schema(description = "管理员修改密码参数")
public class AdminUpdatePasswordDTO {
    // 后期可从 Token 中解析获取，目前由前端传递
    private Long id;

    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}