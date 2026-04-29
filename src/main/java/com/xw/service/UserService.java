package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.UpdatePasswordDTO;
import com.xw.dto.UserUpdateDTO;
import com.xw.vo.UserVO;

/**
 * 用户服务接口
 *
 * @author XW
 */
public interface UserService {

    /**
     * 获取用户完整信息
     *
     * @param userId 用户ID
     * @return 用户完整信息VO
     */
    Result<UserVO> getUserInfo(Long userId);

    /**
     * 更新用户基础信息
     *
     * @param userId    用户ID（从上下文获取，确保安全）
     * @param updateDTO 更新信息DTO
     * @return 操作结果
     */
    Result<String> updateUserInfo(Long userId, UserUpdateDTO updateDTO);

    /**
     * 修改用户密码
     *
     * @param userId 用户ID（从上下文获取，确保安全）
     * @param dto    密码修改DTO
     * @return 操作结果
     */
    Result<String> updatePassword(Long userId, UpdatePasswordDTO dto);

    /**
     * 注销用户账户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<String> deleteAccount(Long userId);
}