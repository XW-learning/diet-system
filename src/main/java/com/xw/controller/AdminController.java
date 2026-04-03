package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.AdminLoginDTO;
import com.xw.dto.AdminUpdatePasswordDTO;
import com.xw.service.AdminService;
import com.xw.vo.AdminVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XW
 */
@Tag(name = "管理员模块")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "40. 管理员登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody AdminLoginDTO dto) {
        return adminService.login(dto);
    }

    @Operation(summary = "41. 获取管理员信息")
    @GetMapping("/info")
    public Result<AdminVO> getInfo(@RequestParam Long id) {
        return adminService.getInfo(id);
    }

    @Operation(summary = "42. 修改密码")
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody AdminUpdatePasswordDTO dto) {
        return adminService.updatePassword(dto);
    }
}