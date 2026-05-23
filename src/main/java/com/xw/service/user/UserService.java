package com.xw.service.user;

import com.xw.dto.user.UpdatePasswordDTO;
import com.xw.dto.user.UserUpdateDTO;
import com.xw.vo.user.UserVO;

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
