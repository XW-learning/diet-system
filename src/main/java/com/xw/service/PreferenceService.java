package com.xw.service;

import com.xw.common.Result;
import com.xw.entity.UserPreference;

/**
 * 饮食偏好业务接口
 * @author XW
 */
public interface PreferenceService {

    /**
     * 获取指定用户的饮食偏好
     * @param userId 用户ID
     * @return 偏好实体
     */
    Result<UserPreference> getPreference(Long userId);

    /**
     * 保存或更新用户饮食偏好
     */
    Result<String> savePreference(com.xw.dto.UserPreferenceDTO dto);

    /**
     * 添加过敏食材记录
     */
    Result<String> addAllergy(Long userId, Long materialId);

    /**
     * 删除指定的过敏食材记录
     */
    Result<String> deleteAllergy(Long userId, Long materialId);

    /**
     * 获取用户的过敏食材列表
     */
    Result<java.util.List<com.xw.vo.AllergyVO>> getUserAllergies(Long userId);
}