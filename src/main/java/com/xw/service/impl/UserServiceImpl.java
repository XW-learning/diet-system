package com.xw.service.impl;

import com.xw.common.Result;
import com.xw.mapper.UserMapper;
import com.xw.service.UserService;
import com.xw.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<UserVO> getUserInfo(Long userId) {
        UserVO vo = userMapper.getUserFullInfo(userId);

        // 2. 判断用户是否存在
        if (vo == null) {
            return Result.error("用户不存在");
        }
        return Result.success(vo);
    }
}