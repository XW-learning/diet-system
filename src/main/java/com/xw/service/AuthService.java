package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.LoginRequest;    // 导入登录 DTO
import com.xw.dto.RegisterRequest; // 导入注册 DTO

public interface AuthService {
    /**
     * 用户注册 - 参数必须改为 RegisterRequest
     */
    Result<String> register(RegisterRequest regRequest);

    /**
     * 用户登录 - 参数建议同步改为 LoginRequest [cite: 193]
     */
    Result<String> login(LoginRequest loginRequest);
}