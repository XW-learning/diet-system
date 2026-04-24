package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.UpdatePasswordDTO;
import com.xw.dto.UserUpdateDTO;
import com.xw.vo.UserVO;

/**
 * 用户服务接口
 * @author XW
 */
public interface UserService {

    Result<UserVO> getUserInfo(Long userId);

    // 🌟 核心修改：接收上下文安全 userId
    Result<String> updateUserInfo(Long userId, UserUpdateDTO updateDTO);

    // 🌟 核心修改：接收上下文安全 userId
    Result<String> updatePassword(Long userId, UpdatePasswordDTO dto);

    Result<String> deleteAccount(Long userId);
}