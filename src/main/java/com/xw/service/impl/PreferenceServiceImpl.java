package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.UserPreferenceDTO;
import com.xw.entity.UserAllergy;
import com.xw.entity.UserPreference;
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
    public Result<UserPreference> getPreference(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId);

        UserPreference preference = preferenceMapper.selectOne(wrapper);

        // 如果用户还没有设置过偏好，返回一个空对象给前端
        if (preference == null) {
            return Result.success(new UserPreference());
        }

        return Result.success(preference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> savePreference(Long userId, UserPreferenceDTO dto) { // 🌟 接收安全的 userId
        // 1. 移除 dto.getUserId() 的基础校验

        // 2. 查询数据库中是否已经有该用户的偏好记录
        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId); // 🌟 使用安全的 userId
        UserPreference existingPreference = preferenceMapper.selectOne(wrapper);

        // 3. 核心逻辑：有则更新，无则插入
        if (existingPreference != null) {
            // ---- 执行更新逻辑 ----
            existingPreference.setTaste(dto.getTaste());
            existingPreference.setDietType(dto.getDietType());
            preferenceMapper.updateById(existingPreference);
            return Result.success("偏好修改成功");
        } else {
            // ---- 执行插入逻辑 ----
            UserPreference newPreference = new UserPreference();
            newPreference.setUserId(userId); // 🌟 强行绑定当前登录人，防越权
            newPreference.setTaste(dto.getTaste());
            newPreference.setDietType(dto.getDietType());
            newPreference.setCreateTime(LocalDateTime.now());

            preferenceMapper.insert(newPreference);
            return Result.success("偏好保存成功");
        }
    }

    @Override
    public Result<String> addAllergy(Long userId, Long materialId) {
        if (userId == null || materialId == null) {
            return Result.error("参数不能为空");
        }

        // 防重复校验
        LambdaQueryWrapper<UserAllergy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAllergy::getUserId, userId)
                .eq(UserAllergy::getMaterialId, materialId);
        if (allergyMapper.selectCount(wrapper) > 0) {
            return Result.error("您已经添加过该过敏食材了");
        }

        UserAllergy newAllergy = new UserAllergy();
        newAllergy.setUserId(userId);
        newAllergy.setMaterialId(materialId);
        newAllergy.setCreateTime(LocalDateTime.now());

        allergyMapper.insert(newAllergy);

        return Result.success("过敏食材添加成功");
    }

    @Override
    public Result<String> deleteAllergy(Long userId, Long materialId) {
        if (userId == null || materialId == null) {
            return Result.error("参数不能为空");
        }

        LambdaQueryWrapper<UserAllergy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAllergy::getUserId, userId)
                .eq(UserAllergy::getMaterialId, materialId);

        int rows = allergyMapper.delete(wrapper);

        return rows > 0 ? Result.success("过敏食材删除成功") : Result.error("未找到该过敏记录");
    }

    @Override
    public Result<List<AllergyVO>> getUserAllergies(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        List<AllergyVO> allergyList = allergyMapper.getUserAllergies(userId);
        return Result.success(allergyList);
    }
}