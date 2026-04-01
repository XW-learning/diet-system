package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.TargetDTO;
import com.xw.entity.UserTarget;

public interface TargetService {
    /**
     * 获取用户最新的目标
     */
    Result<UserTarget> getTarget(Long userId);

    /**
     * 保存或更新用户目标
     */
    Result<String> saveTarget(TargetDTO dto);

    /**
     * 删除用户目标 (清除目标)
     */
    Result<String> deleteTarget(Long userId);
}