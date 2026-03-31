package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.LoginRequest;
import com.xw.dto.RegisterRequest;
import com.xw.dto.ResetPasswordRequest;
import com.xw.entity.User;
import com.xw.mapper.UserMapper;
import com.xw.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<String> register(RegisterRequest regRequest) {
        // 1. 模拟校验验证码（后期可接入阿里云/腾讯云短信服务）
        if (!"123456".equals(regRequest.getCaptcha())) {
            return Result.error("验证码错误");
        }

        // 2. 校验手机号是否已存在
        // LambdaQueryWrapper 能够有效防止 SQL 注入并简化字段编写
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, regRequest.getPhone());
        if (userMapper.selectCount(queryWrapper) > 0) {
            return Result.error("该手机号已被注册");
        }

        // 3. 将 DTO 转换为 Entity [cite: 24]
        User user = new User();
        // 添加手机号
        user.setPhone(regRequest.getPhone());
        // 添加用户名
        user.setUsername(regRequest.getUsername());
        // 注意：后续需集成 BCrypt 加密存储密码以符合安全规范 [cite: 151]
        user.setPassword(regRequest.getPassword());
        // 默认状态为正常（1-正常, 0-禁用）
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        // 4. 插入数据库并返回结果
        int rows = userMapper.insert(user);
        return rows > 0 ? Result.success("注册成功") : Result.error("服务器异常，注册失败");
    }

    @Override
    public Result<String> login(LoginRequest loginRequest) { // 改为接收 DTO
        // 1. 根据手机号查询用户 [cite: 25]
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, loginRequest.getPhone());
        User user = userMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在及密码是否匹配
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return Result.error("手机号或密码错误");
        }

        // 3. 校验账号状态（管理员可在后台禁用违规用户）[cite: 96]
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用，请联系管理员");
        }

        // 4. 生成 Token 并返回 [cite: 187]
        // 实际开发中会使用 JWT 库（如 jjwt）生成加密字符串
        String mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.user_" + user.getId();
        return Result.success(mockToken);
    }

    @Override
    public Result<String> resetPassword(ResetPasswordRequest resetRequest) {
        // 1. 模拟校验验证码 [cite: 25]
        if (!"123456".equals(resetRequest.getCaptcha())) {
            return Result.error("验证码错误");
        }

        // 2. 根据手机号查找用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, resetRequest.getPhone());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 更新密码 [cite: 29]
        user.setPassword(resetRequest.getNewPassword());
        userMapper.updateById(user);

        return Result.success("密码重置成功，请重新登录");
    }

    @Override
    public Result<String> logout() {
        // 因为目前使用模拟 Token，后端只需返回成功。
        // 后期如果引入 Redis 黑名单机制，需要在这里失效 Token 。
        return Result.success("已成功退出登录");
    }
}