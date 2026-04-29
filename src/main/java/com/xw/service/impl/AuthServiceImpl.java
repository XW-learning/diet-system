package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.LoginDTO;
import com.xw.dto.RegisterDTO;
import com.xw.dto.ResetPasswordDTO;
import com.xw.entity.User;
import com.xw.mapper.UserMapper;
import com.xw.service.AuthService;
import com.xw.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 *
 * @author XW
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param regRequest 注册请求DTO
     * @return 注册结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> register(RegisterDTO regRequest) {
        if (!"123456".equals(regRequest.getCaptcha())) {
            return Result.error("验证码错误");
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, regRequest.getPhone());
        if (userMapper.selectCount(queryWrapper) > 0) {
            return Result.error("该手机号已被注册");
        }

        User user = new User();
        user.setPhone(regRequest.getPhone());
        user.setUsername(regRequest.getUsername());
        user.setPassword(regRequest.getPassword());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        int rows = userMapper.insert(user);
        return rows > 0 ? Result.success("注册成功") : Result.error("服务器异常，注册失败");
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求DTO
     * @return 登录结果，包含JWT Token
     */
    @Override
    public Result<String> login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, loginDTO.getPhone());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null || !user.getPassword().equals(loginDTO.getPassword())) {
            return Result.error("手机号或密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用，请联系管理员");
        }

        // 使用JwtUtil生成真正的JWT Token
        String token = JwtUtil.generateUserToken(user.getId());
        return Result.success(token);
    }

    /**
     * 重置密码
     *
     * @param resetRequest 重置密码请求DTO
     * @return 重置结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> resetPassword(ResetPasswordDTO resetRequest) {
        if (!"123456".equals(resetRequest.getCaptcha())) {
            return Result.error("验证码错误");
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, resetRequest.getPhone());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setPassword(resetRequest.getNewPassword());
        userMapper.updateById(user);
        return Result.success("密码重置成功，请重新登录");
    }

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @Override
    public Result<String> logout() {
        return Result.success("已成功退出登录");
    }
}