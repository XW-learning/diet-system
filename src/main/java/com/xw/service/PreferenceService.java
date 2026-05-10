package com.xw.service;

import com.xw.dto.UserPreferenceDTO;
import com.xw.entity.UserPreference;
import com.xw.vo.AllergyVO;

import java.util.List;

/**
 * 用户偏好与过敏源服务接口
 * @author XW
 */
public interface PreferenceService {

    UserPreference getPreference(Long userId);

    String savePreference(Long userId, UserPreferenceDTO dto);

    String addAllergy(Long userId, Long materialId);

    String deleteAllergy(Long userId, Long materialId);

    List<AllergyVO> getUserAllergies(Long userId);
}
