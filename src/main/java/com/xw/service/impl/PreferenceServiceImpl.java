package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.dto.UserPreferenceDTO;
import com.xw.entity.UserAllergy;
import com.xw.entity.UserPreference;
import com.xw.exception.BusinessException;
import com.xw.mapper.UserAllergyMapper;
import com.xw.mapper.UserPreferenceMapper;
import com.xw.service.PreferenceService;
import com.xw.vo.AllergyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author XW
 */
@Service
public class PreferenceServiceImpl implements PreferenceService {

    @Autowired
    private UserPreferenceMapper preferenceMapper;
    @Autowired
    private UserAllergyMapper allergyMapper;

    @Override
    public UserPreference getPreference(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId);

        UserPreference preference = preferenceMapper.selectOne(wrapper);

        if (preference == null) {
            return new UserPreference();
        }

        return preference;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String savePreference(Long userId, UserPreferenceDTO dto) {

        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId);
        UserPreference existingPreference = preferenceMapper.selectOne(wrapper);

        if (existingPreference != null) {
            existingPreference.setTaste(dto.getTaste());
            existingPreference.setDietType(dto.getDietType());
            preferenceMapper.updateById(existingPreference);
            return "偏好修改成功";
        } else {
            UserPreference newPreference = new UserPreference();
            newPreference.setUserId(userId);
            newPreference.setTaste(dto.getTaste());
            newPreference.setDietType(dto.getDietType());
            newPreference.setCreateTime(LocalDateTime.now());

            preferenceMapper.insert(newPreference);
            return "偏好保存成功";
        }
    }

    @Override
    public String addAllergy(Long userId, Long materialId) {
        if (userId == null || materialId == null) {
            throw new BusinessException("参数不能为空");
        }

        LambdaQueryWrapper<UserAllergy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAllergy::getUserId, userId)
                .eq(UserAllergy::getMaterialId, materialId);
        if (allergyMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("您已经添加过该过敏食材了");
        }

        UserAllergy newAllergy = new UserAllergy();
        newAllergy.setUserId(userId);
        newAllergy.setMaterialId(materialId);
        newAllergy.setCreateTime(LocalDateTime.now());

        allergyMapper.insert(newAllergy);

        return "过敏食材添加成功";
    }

    @Override
    public String deleteAllergy(Long userId, Long materialId) {
        if (userId == null || materialId == null) {
            throw new BusinessException("参数不能为空");
        }

        LambdaQueryWrapper<UserAllergy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAllergy::getUserId, userId)
                .eq(UserAllergy::getMaterialId, materialId);

        int rows = allergyMapper.delete(wrapper);

        if (rows <= 0) throw new BusinessException("未找到该过敏记录");
        return "过敏食材删除成功";
    }

    @Override
    public List<AllergyVO> getUserAllergies(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        return allergyMapper.getUserAllergies(userId);
    }
}
