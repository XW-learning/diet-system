package com.xw.controller;

import com.xw.annotation.LogOperation;
import com.xw.common.Result;
import com.xw.dto.UpdatePasswordDTO;
import com.xw.dto.UserUpdateDTO;
import com.xw.service.UserService;
import com.xw.utils.ThreadLocalUtil;
import com.xw.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 *
 * @author XW
 */
@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户完整信息
     *
     * @return 用户完整信息VO
     */
    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return userService.getUserInfo(currentUserId);
    }

    /**
     * 保存用户基础信息
     *
     * @param updateDTO 用户更新信息DTO
     * @return 操作结果
     */
    @Operation(summary = "保存用户基础信息")
    @LogOperation("保存用户信息")
    @PostMapping("/save")
    public Result<String> updateUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return userService.updateUserInfo(currentUserId, updateDTO);
    }

    /**
     * 修改密码
     *
     * @param dto 密码修改DTO
     * @return 操作结果
     */
    @Operation(summary = "修改密码")
    @LogOperation("修改密码")
    @PutMapping("/updatePassword")
    public Result<String> updatePassword(@RequestBody UpdatePasswordDTO dto) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return userService.updatePassword(userId, dto);
    }

    /**
     * 注销账户
     *
     * @return 操作结果
     */
    @Operation(summary = "注销账户")
    @LogOperation("注销账户")
    @DeleteMapping("/delete")
    public Result<String> deleteAccount() {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return userService.deleteAccount(userId);
    }
}