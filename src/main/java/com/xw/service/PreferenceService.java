package com.xw.service;

import com.xw.common.Result;
import com.xw.dto.UserPreferenceDTO;
import com.xw.entity.UserPreference;
import com.xw.vo.AllergyVO;

import java.util.List;

/**
 * 用户偏好与过敏源服务接口
 * @author XW
 */
public interface PreferenceService {

    Result<UserPreference> getPreference(Long userId);

    // 🌟 核心安全修改：加入上下文 userId，不再信任 DTO 传参
    Result<String> savePreference(Long userId, UserPreferenceDTO dto);

    Result<String> addAllergy(Long userId, Long materialId);

    Result<String> deleteAllergy(Long userId, Long materialId);

    Result<List<AllergyVO>> getUserAllergies(Long userId);
}