package com.xw.controller.admin;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.dto.admin.AdminLoginDTO;
import com.xw.dto.admin.AdminUpdatePasswordDTO;
import com.xw.dto.admin.AdminUserQueryDTO;
import com.xw.service.admin.AdminService;
import com.xw.vo.admin.AdminVO;
import com.xw.vo.admin.AdminUserVO;
import com.xw.common.PageResult;
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
    @LogOperation("管理员登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody AdminLoginDTO dto) {
        return Result.success(adminService.login(dto));
    }

    @Operation(summary = "41. 获取管理员信息")
    @GetMapping("/info")
    public Result<AdminVO> getInfo(@RequestParam Long id) {
        return Result.success(adminService.getInfo(id));
    }

    @Operation(summary = "42. 修改密码")
    @LogOperation("修改密码")
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody AdminUpdatePasswordDTO dto) {
        return Result.success(adminService.updatePassword(dto));
    }

    @Operation(summary = "43. 获取用户列表（分页+搜索）")
    @GetMapping("/user/list")
    public Result<PageResult<AdminUserVO>> getUserList(AdminUserQueryDTO queryDTO) {
        return Result.success(adminService.getUserList(queryDTO));
    }

    @Operation(summary = "44. 查看用户详情")
    @GetMapping("/user/detail")
    public Result<AdminUserVO> getUserDetail(@RequestParam Long userId) {
        return Result.success(adminService.getUserDetail(userId));
    }

    @Operation(summary = "45. 禁用/启用用户")
    @LogOperation("禁用/启用用户")
    @PutMapping("/user/status")
    public Result<String> updateUserStatus(@RequestParam Long userId, @RequestParam Integer status) {
        return Result.success(adminService.updateUserStatus(userId, status));
    }

    @Operation(summary = "46. 删除用户")
    @LogOperation("删除用户")
    @DeleteMapping("/user/delete")
    public Result<String> deleteUser(@RequestParam Long userId) {
        return Result.success(adminService.deleteUser(userId));
    }
}
