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
    @Schema(description = "管理员ID", required = true)
    private Long id;

    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码", example = "123456")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码", example = "654321")
    private String newPassword;
}
