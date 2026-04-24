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
 * @author XW
 */
@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息接口
     * 目前通过 RequestParam 传递 ID，后期可改为从 Token 中解析
     */
    @Operation(summary = "获取用户信息(全量聚合)")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        // 打印id
        System.out.println("用户id:" + currentUserId);
        return userService.getUserInfo(currentUserId);
    }

    @Operation(summary = "保存用户基础信息")
    @LogOperation("保存用户信息")
    @PostMapping("/save")
    public Result<String> updateUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        Long currentUserId = ThreadLocalUtil.getCurrentUserId();
        return userService.updateUserInfo(currentUserId, updateDTO);
    }

    @Operation(summary = "修改密码")
    @LogOperation("修改密码")
    @PutMapping("/updatePassword")
    public Result<String> updatePassword(@RequestBody UpdatePasswordDTO dto) {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return userService.updatePassword(userId, dto);
    }

    @Operation(summary = "注销账户")
    @LogOperation("注销账户")
    @DeleteMapping("/delete")
    public Result<String> deleteAccount() {
        Long userId = ThreadLocalUtil.getCurrentUserId();
        return userService.deleteAccount(userId);
    }
}