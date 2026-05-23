package com.xw.service.user;

import com.xw.dto.user.UserPreferenceDTO;
import com.xw.entity.user.UserPreference;
import com.xw.vo.user.AllergyVO;

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
