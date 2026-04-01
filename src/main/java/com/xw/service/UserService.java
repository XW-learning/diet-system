package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.UpdatePasswordDTO;
import com.xw.dto.UserUpdateDTO;
import com.xw.vo.UserVO;

/**
 * @author XW
 */
public interface UserService {
    /**
     * 获取用户的全量聚合信息（基础资料+最新身材+目标+偏好）
     * @param userId 用户ID
     * @return 聚合后的 UserVO
     */
    Result<UserVO> getUserInfo(Long userId);

    /**
     * 修改用户基础信息 (动态更新)
     */
    Result<String> updateUserInfo(UserUpdateDTO updateDTO);

    /**
     * 修改用户密码（需验证旧密码）
     */
    Result<String> updatePassword(UpdatePasswordDTO dto);

    /**
     * 注销账户（物理删除用户及其所有关联数据）
     */
    Result<String> deleteAccount(Long userId);
}