package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.TargetDTO;
import com.xw.entity.UserTarget;
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
    public Result<UserTarget> getTarget(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 查询最新的一个目标 (按时间倒序排，取第一条)
        LambdaQueryWrapper<UserTarget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTarget::getUserId, userId)
                .orderByDesc(UserTarget::getCreateTime)
                .last("LIMIT 1"); // 🌟 魔法指令：只取最新的一条

        UserTarget target = targetMapper.selectOne(wrapper);

        // 如果没设过目标，返回空对象防前端报错
        return Result.success(target != null ? target : new UserTarget());
    }

    @Override
    public Result<String> saveTarget(TargetDTO dto) {
        if (dto.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        // 1. 先查有没有旧目标
        LambdaQueryWrapper<UserTarget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTarget::getUserId, dto.getUserId())
                .orderByDesc(UserTarget::getCreateTime)
                .last("LIMIT 1");
        UserTarget existing = targetMapper.selectOne(wrapper);

        if (existing != null) {
            // 2. 有则更新 (SaveOrUpdate)
            existing.setTargetWeight(dto.getTargetWeight());
            existing.setTargetDate(dto.getTargetDate());
            existing.setGoalType(dto.getGoalType());
            targetMapper.updateById(existing);
            return Result.success("目标更新成功，继续加油！");
        } else {
            // 3. 无则插入
            UserTarget newTarget = new UserTarget();
            BeanUtils.copyProperties(dto, newTarget);
            newTarget.setCreateTime(LocalDateTime.now());
            targetMapper.insert(newTarget);
            return Result.success("新目标设定成功，向着目标冲刺吧！");
        }
    }

    @Override
    public Result<String> deleteTarget(Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 删除该用户的所有目标记录
        LambdaQueryWrapper<UserTarget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTarget::getUserId, userId);
        int rows = targetMapper.delete(wrapper);

        return rows > 0 ? Result.success("目标已清除") : Result.error("您还没有设定过目标");
    }
}