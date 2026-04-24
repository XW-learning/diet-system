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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveRecord(Long userId, BodyRecordDTO dto) { // 🌟 接收安全上下文 userId
        // 1. 不再需要校验 dto.getUserId()，绝对信任拦截器传来的 userId

        // 2. 确定记录的真实时间 (处理前端传来的补打卡日期)
        LocalDateTime targetTime = dto.getRecordTime() != null ? dto.getRecordTime() : LocalDateTime.now();
        LocalDate targetDate = targetTime.toLocalDate();

        // 3. 查该用户在这一天是否已经有过记录
        LambdaQueryWrapper<UserBodyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBodyRecord::getUserId, userId) // 🌟 替换 dto.getUserId()
                .ge(UserBodyRecord::getRecordTime, targetDate.atStartOfDay())
                .le(UserBodyRecord::getRecordTime, targetDate.atTime(23, 59, 59));

        List<UserBodyRecord> existingRecords = bodyRecordMapper.selectList(wrapper);

        UserBodyRecord record;
        boolean isUpdate = false;

        if (!existingRecords.isEmpty()) {
            record = existingRecords.get(existingRecords.size() - 1);
            isUpdate = true;
        } else {
            record = new UserBodyRecord();
            record.setUserId(userId); // 🌟 替换 dto.getUserId()
            record.setRecordTime(targetTime);
            record.setCreateTime(LocalDateTime.now());

            // 查找该用户最后一次提交过体重的记录
            LambdaQueryWrapper<UserBodyRecord> lastDataWrapper = new LambdaQueryWrapper<>();
            lastDataWrapper.eq(UserBodyRecord::getUserId, userId) // 🌟 替换 dto.getUserId()
                    .isNotNull(UserBodyRecord::getWeight)
                    .orderByDesc(UserBodyRecord::getRecordTime)
                    .last("LIMIT 1");
            UserBodyRecord lastRecord = bodyRecordMapper.selectOne(lastDataWrapper);

            if (lastRecord != null) {
                record.setWeight(lastRecord.getWeight());
                record.setHeight(lastRecord.getHeight());
                record.setBmi(lastRecord.getBmi());
                record.setWaist(lastRecord.getWaist());
                record.setHip(lastRecord.getHip());
                record.setChest(lastRecord.getChest());
            }
        }

        // 4. 独立处理体重、身高与 BMI
        if (dto.getWeight() != null) {
            User user = userMapper.selectById(userId); // 🌟 替换 dto.getUserId()
            if (user == null) {
                return Result.error("用户不存在");
            }
            BigDecimal currentHeight = user.getHeight();

            if (currentHeight == null || currentHeight.compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error("缺少身高数据，请先前往「个人中心」设置身高！");
            }

            record.setWeight(dto.getWeight());
            record.setHeight(currentHeight);

            BigDecimal heightInMeter = currentHeight.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = dto.getWeight().divide(heightInMeter.pow(2), 2, RoundingMode.HALF_UP);
            record.setBmi(bmi);
        }

        // 5. 处理三围数据
        if (dto.getWaist() != null) record.setWaist(dto.getWaist());
        if (dto.getHip() != null) record.setHip(dto.getHip());
        if (dto.getChest() != null) record.setChest(dto.getChest());

        // 6. 处理经期起止时间
        if (Boolean.TRUE.equals(dto.getIsPeriodUpdate())) {
            record.setPeriodStartDate(dto.getPeriodStartDate());
            record.setPeriodEndDate(dto.getPeriodEndDate());
        } else {
            if (dto.getPeriodStartDate() != null) record.setPeriodStartDate(dto.getPeriodStartDate());
            if (dto.getPeriodEndDate() != null) record.setPeriodEndDate(dto.getPeriodEndDate());
        }

        // 7. 执行落库
        if (isUpdate) {
            bodyRecordMapper.updateById(record);
        } else {
            bodyRecordMapper.insert(record);
        }

        if (dto.getWeight() != null) {
            return Result.success("身材打卡成功！当前BMI：" + record.getBmi());
        } else if (dto.getPeriodStartDate() != null || dto.getPeriodEndDate() != null) {
            return Result.success("生理期记录成功！");
        } else {
            return Result.success("数据保存成功！");
        }
    }

    @Override
    public Result<List<UserBodyRecord>> getRecordList(Long userId) {
        // userId 已经由拦截器解析，不可能为 null，但保留防御性编程也可以
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        LambdaQueryWrapper<UserBodyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBodyRecord::getUserId, userId)
                .orderByDesc(UserBodyRecord::getRecordTime);
        List<UserBodyRecord> list = bodyRecordMapper.selectList(wrapper);
        return Result.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteRecord(Long userId, Long id) { // 🌟 加入 userId
        if (id == null) {
            return Result.error("记录ID不能为空");
        }

        // 🌟 越权校验：根据前端传来的 ID 去数据库查出这条记录
        UserBodyRecord record = bodyRecordMapper.selectById(id);
        if (record == null) {
            return Result.error("该记录不存在或已被删除");
        }

        // 🌟 核心安全逻辑：判断这条记录的主人，是不是当前登录的这个 userId
        if (!record.getUserId().equals(userId)) {
            return Result.error("警告：您无权删除他人的记录！");
        }

        int rows = bodyRecordMapper.deleteById(id);
        return rows > 0 ? Result.success("记录删除成功") : Result.error("该记录不存在或已被删除");
    }
}