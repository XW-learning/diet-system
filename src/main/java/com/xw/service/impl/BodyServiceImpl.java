package com.xw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.common.Result;
import com.xw.dto.BodyRecordDTO;
import com.xw.entity.User;
import com.xw.entity.UserBodyRecord;
import com.xw.mapper.UserBodyRecordMapper;
import com.xw.mapper.UserMapper;
import com.xw.service.BodyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author XW
 */
@Service
public class BodyServiceImpl implements BodyService {

    @Autowired
    private UserBodyRecordMapper bodyRecordMapper;

    @Autowired
    private UserMapper userMapper; // 🌟 注入 UserMapper

    @Override
    public Result<String> saveRecord(BodyRecordDTO dto) {
        // 1. 基础参数校验（只强校验 userId 和 weight）
        if (dto.getUserId() == null || dto.getWeight() == null) {
            return Result.error("用户ID、体重不能为空");
        }

        // 🌟 2. 核心改动：从 t_user 表获取该用户的真实身高
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }
        BigDecimal currentHeight = user.getHeight();

        // 如果 t_user 里没存身高，或者身高异常
        if (currentHeight == null || currentHeight.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("缺少身高数据，请先前往「个人中心」设置身高！");
        }

        // 3. 将 DTO 转为 Entity
        UserBodyRecord record = new UserBodyRecord();
        record.setUserId(dto.getUserId());
        record.setWeight(dto.getWeight());
        record.setWaist(dto.getWaist());
        record.setHip(dto.getHip());
        record.setHeight(currentHeight); // 🌟 存入历史快照，供以后追溯
        record.setChest(dto.getChest()); // 🌟 新增胸围字段赋值

        record.setRecordTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());

        // 4. 计算 BMI
        // 身高单位换算 cm -> m
        BigDecimal heightInMeter = currentHeight.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal heightSquared = heightInMeter.pow(2);
        // BMI = 体重 / 身高^2
        BigDecimal bmi = dto.getWeight().divide(heightSquared, 2, RoundingMode.HALF_UP);
        record.setBmi(bmi);

        // 5. 落库保存 (继续使用 insert，保留历史记录)
        bodyRecordMapper.insert(record);

        return Result.success("身材打卡成功！当前BMI：" + bmi);
    }

    @Override
    public Result<List<UserBodyRecord>> getRecordList(Long userId) {
        // 1. 参数校验
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 构造查询条件：查该用户的记录，并且按记录时间倒序排列 (最新的在最上面)
        LambdaQueryWrapper<UserBodyRecord> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(UserBodyRecord::getUserId, userId)
                // 按时间倒序
                .orderByDesc(UserBodyRecord::getRecordTime);

        // 3. 执行查询
        List<UserBodyRecord> list = bodyRecordMapper.selectList(wrapper);

        // 4. 返回结果
        return Result.success(list);
    }

    @Override
    public Result<String> deleteRecord(Long id) {
        // 1. 参数校验
        if (id == null) {
            return Result.error("记录ID不能为空");
        }

        // 2. 直接根据主键 ID 进行物理删除
        int rows = bodyRecordMapper.deleteById(id);

        // 3. 判断是否删除成功
        return rows > 0 ? Result.success("记录删除成功") : Result.error("该记录不存在或已被删除");
    }
}