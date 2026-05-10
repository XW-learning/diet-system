package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.dto.TargetDTO;
import com.xw.entity.UserTarget;
import com.xw.exception.BusinessException;
import com.xw.mapper.UserTargetMapper;
import com.xw.service.TargetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author XW
 */
@Service
public class TargetServiceImpl implements TargetService {

    @Autowired
    private UserTargetMapper targetMapper;

    @Override
    public UserTarget getTarget(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        LambdaQueryWrapper<UserTarget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTarget::getUserId, userId)
                .orderByDesc(UserTarget::getCreateTime)
                .last("LIMIT 1");

        UserTarget target = targetMapper.selectOne(wrapper);

        return target != null ? target : new UserTarget();
    }

    @Override
    public String saveTarget(Long userId, TargetDTO dto) {

        LambdaQueryWrapper<UserTarget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTarget::getUserId, userId)
                .orderByDesc(UserTarget::getCreateTime)
                .last("LIMIT 1");
        UserTarget existing = targetMapper.selectOne(wrapper);

        if (existing != null) {
            existing.setTargetWeight(dto.getTargetWeight());
            existing.setTargetDate(dto.getTargetDate());
            existing.setGoalType(dto.getGoalType());
            targetMapper.updateById(existing);
            return "目标更新成功，继续加油！";
        } else {
            UserTarget newTarget = new UserTarget();
            BeanUtils.copyProperties(dto, newTarget);
            newTarget.setCreateTime(LocalDateTime.now());
            targetMapper.insert(newTarget);
            return "新目标设定成功，向着目标冲刺吧！";
        }
    }

    @Override
    public String deleteTarget(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        LambdaQueryWrapper<UserTarget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTarget::getUserId, userId);
        int rows = targetMapper.delete(wrapper);

        if (rows <= 0) throw new BusinessException("您还没有设定过目标");
        return "目标已清除";
    }
}
