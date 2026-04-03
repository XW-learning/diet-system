package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.LoginDTO;
import com.xw.dto.RegisterDTO;
import com.xw.dto.ResetPasswordDTO;
import com.xw.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XW
 */
@Tag(name = "认证模块")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO regRequest) {
        return authService.register(regRequest);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @Operation(summary = "重置密码")
    @PutMapping("/resetPassword") // 注意根据文档使用 PUT 方法
    public Result<String> resetPassword(@RequestBody ResetPasswordDTO resetRequest) {
        return authService.resetPassword(resetRequest);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout") // 对应文档 POST 方法
    public Result<String> logout() {
        return authService.logout();
    }
}