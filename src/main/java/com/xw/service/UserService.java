package com.xw.service;

import com.xw.dto.UpdatePasswordDTO;
import com.xw.dto.UserUpdateDTO;
import com.xw.vo.UserVO;

/**
 * 用户服务接口
 *
 * @author XW
 */
public interface UserService {

    UserVO getUserInfo(Long userId);

    String updateUserInfo(Long userId, UserUpdateDTO updateDTO);

    String updatePassword(Long userId, UpdatePasswordDTO dto);

    String deleteAccount(Long userId);
}
