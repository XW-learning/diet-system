package com.xw.controller;

import com.xw.common.Result;
import com.xw.dto.UpdatePasswordDTO;
import com.xw.dto.UserUpdateDTO;
import com.xw.service.UserService;
import com.xw.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result<UserVO> getUserInfo(@RequestParam Long id) {
        return userService.getUserInfo(id);
    }

    @Operation(summary = "修改用户基础信息")
    @PostMapping("/save")
    public Result<String> updateUserInfo(@RequestBody UserUpdateDTO updateDTO) {
        return userService.updateUserInfo(updateDTO);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/updatePassword")
    public Result<String> updatePassword(@RequestBody UpdatePasswordDTO updatedpwdDTO) {
        return userService.updatePassword(updatedpwdDTO);
    }

    @Operation(summary = "注销账户")
    @DeleteMapping("/delete")
    public Result<String> deleteAccount(@RequestParam Long id) {
        return userService.deleteAccount(id);
    }
}