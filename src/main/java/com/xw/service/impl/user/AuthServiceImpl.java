package com.xw.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.dto.user.LoginDTO;
import com.xw.dto.user.RegisterDTO;
import com.xw.dto.user.ResetPasswordDTO;
import com.xw.entity.user.User;
import com.xw.exception.BusinessException;
import com.xw.mapper.user.UserMapper;
import com.xw.service.user.AuthService;
import com.xw.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterDTO regRequest) {
        if (!"123456".equals(regRequest.getCaptcha())) {
            throw new BusinessException("验证码错误");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, regRequest.getPhone());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("该手机号已被注册");
        }
        User user = new User();
        user.setPhone(regRequest.getPhone());
        user.setUsername(regRequest.getUsername());
        user.setPassword(regRequest.getPassword());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        int rows = userMapper.insert(user);
        if (rows <= 0) throw new BusinessException("服务器异常，注册失败");
        return "注册成功";
    }

    @Override
    public String login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, loginDTO.getPhone());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null || !user.getPassword().equals(loginDTO.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }
        return JwtUtil.generateUserToken(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String resetPassword(ResetPasswordDTO resetRequest) {
        if (!"123456".equals(resetRequest.getCaptcha())) {
            throw new BusinessException("验证码错误");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, resetRequest.getPhone());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(resetRequest.getNewPassword());
        userMapper.updateById(user);
        return "密码重置成功，请重新登录";
    }

    @Override
    public String logout() {
        return "已成功退出登录";
    }
}
