package com.xw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改密码 DTO
 * @author XW
 */
@Data
public class UpdatePasswordDTO {
    // 当前登录用户的ID
    private Long id;

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}