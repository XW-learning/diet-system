package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.LoginRequest;
import com.xw.dto.RegisterRequest;
import com.xw.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证模块")
@RestController
@RequestMapping("/api/auth") // 对应 API 文档 Base URL [cite: 185, 189]
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册接口 [cite: 191]
     * 使用 @RequestBody 接收前端传来的 JSON 格式注册数据
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest regRequest) {
        return authService.register(regRequest);
    }

    /**
     * 用户登录接口 [cite: 193]
     * 将整个 loginRequest 对象传递给 Service 层，保持层级间数据流转的统一性
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}