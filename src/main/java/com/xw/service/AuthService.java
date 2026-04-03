package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.LoginDTO;
import com.xw.dto.RegisterDTO;
import com.xw.dto.ResetPasswordDTO;

public interface AuthService {
    /**
     * 用户注册 - 参数必须改为 RegisterDTO
     */
    Result<String> register(RegisterDTO regRequest);

    /**
     * 用户登录 - 参数建议同步改为 LoginDTO [cite: 193]
     */
    Result<String> login(LoginDTO loginDTO);

    /**
     * 重置密码
     */
    Result<String> resetPassword(ResetPasswordDTO resetRequest);

    /**
     * 退出登录
     */
    Result<String> logout();
}