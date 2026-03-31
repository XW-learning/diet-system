package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.LoginRequest;
import com.xw.dto.RegisterRequest;
import com.xw.dto.ResetPasswordRequest;
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

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest regRequest) {
        return authService.register(regRequest);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @Operation(summary = "重置密码")
    @PutMapping("/resetPassword") // 注意根据文档使用 PUT 方法
    public Result<String> resetPassword(@RequestBody ResetPasswordRequest resetRequest) {
        return authService.resetPassword(resetRequest);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout") // 对应文档 POST 方法
    public Result<String> logout() {
        return authService.logout();
    }
}