package com.xw.service.impl;

import com.xw.common.Result;
import com.xw.dto.BodyRecordDTO;
import com.xw.entity.UserBodyRecord;
import com.xw.mapper.UserBodyRecordMapper;
import com.xw.service.BodyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Service
public class BodyServiceImpl implements BodyService {

    @Autowired
    private UserBodyRecordMapper bodyRecordMapper;

    @Override
    public Result<String> saveRecord(BodyRecordDTO dto) {
        // 1. 基础参数校验
        if (dto.getUserId() == null || dto.getHeight() == null || dto.getWeight() == null) {
            return Result.error("用户ID、身高、体重不能为空");
        }
        if (dto.getHeight().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error("身高数据异常");
        }

        // 2. 将 DTO 转为 Entity
        UserBodyRecord record = new UserBodyRecord();
        record.setUserId(dto.getUserId());
        record.setHeight(dto.getHeight());
        record.setWeight(dto.getWeight());
        record.setWaist(dto.getWaist());
        record.setHip(dto.getHip());

        // 设置打卡时间（默认就是当前时间）
        record.setRecordTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());

        // 🌟 3. 核心业务逻辑：自动计算 BMI
        // 公式：BMI = 体重(kg) / (身高(m) * 身高(m))
        // 注意：前端传来的身高单位是 cm，需要先除以 100 转成 m
        BigDecimal heightInMeter = dto.getHeight().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // 计算身高的平方 (m^2)
        BigDecimal heightSquared = heightInMeter.pow(2);

        // 计算 BMI 并保留两位小数（采用四舍五入 HALF_UP 策略防报错）
        BigDecimal bmi = dto.getWeight().divide(heightSquared, 2, RoundingMode.HALF_UP);

        // 存入实体类
        record.setBmi(bmi);

        // 4. 落库保存
        bodyRecordMapper.insert(record);

        return Result.success("身材数据打卡成功！当前BMI指数为：" + bmi);
    }

    @Override
    public Result<java.util.List<UserBodyRecord>> getRecordList(Long userId) {
        // 1. 参数校验
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 构造查询条件：查该用户的记录，并且按记录时间倒序排列 (最新的在最上面)
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserBodyRecord> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        wrapper.eq(UserBodyRecord::getUserId, userId)
                // 按时间倒序
                .orderByDesc(UserBodyRecord::getRecordTime);

        // 3. 执行查询
        java.util.List<UserBodyRecord> list = bodyRecordMapper.selectList(wrapper);

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