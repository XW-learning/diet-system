package com.xw.service;

import com.xw.common.Result;
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
}