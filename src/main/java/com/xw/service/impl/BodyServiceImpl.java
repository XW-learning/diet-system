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
    public Result<String> saveRecord(BodyRecordDTO dto) {
        // 1. 基础参数校验
        if (dto.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }

        // 2. 确定记录的真实时间 (处理前端传来的补打卡日期)
        LocalDateTime targetTime = dto.getRecordTime() != null ? dto.getRecordTime() : LocalDateTime.now();
        LocalDate targetDate = targetTime.toLocalDate();

        // 3. 查该用户在这一天是否已经有过记录（Upsert逻辑核心）
        LambdaQueryWrapper<UserBodyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBodyRecord::getUserId, dto.getUserId())
                .ge(UserBodyRecord::getRecordTime, targetDate.atStartOfDay())
                .le(UserBodyRecord::getRecordTime, targetDate.atTime(23, 59, 59));

        List<UserBodyRecord> existingRecords = bodyRecordMapper.selectList(wrapper);

        UserBodyRecord record;
        boolean isUpdate = false;

        // 判断是更新还是新增
        if (!existingRecords.isEmpty()) {
            // 当天已有记录，取最新的一条进行更新追加
            record = existingRecords.get(existingRecords.size() - 1);
            isUpdate = true;
        } else {
            // 当天无记录，准备新增
            record = new UserBodyRecord();
            record.setUserId(dto.getUserId());
            record.setRecordTime(targetTime);
            record.setCreateTime(LocalDateTime.now());

            // 🌟 核心修复：历史数据继承逻辑
            // 查找该用户最后一次提交过体重的记录
            LambdaQueryWrapper<UserBodyRecord> lastDataWrapper = new LambdaQueryWrapper<>();
            lastDataWrapper.eq(UserBodyRecord::getUserId, dto.getUserId())
                    .isNotNull(UserBodyRecord::getWeight) // 确保这天是真的记录了身体数据的
                    .orderByDesc(UserBodyRecord::getRecordTime)
                    .last("LIMIT 1");
            UserBodyRecord lastRecord = bodyRecordMapper.selectOne(lastDataWrapper);

            // 如果以前有数据，把上一天的三围体重全盘继承过来，作为今天的打底数据
            if (lastRecord != null) {
                record.setWeight(lastRecord.getWeight());
                record.setHeight(lastRecord.getHeight());
                record.setBmi(lastRecord.getBmi());
                record.setWaist(lastRecord.getWaist());
                record.setHip(lastRecord.getHip());
                record.setChest(lastRecord.getChest());
            }
        }

        // 4. 独立处理体重、身高与 BMI (如果今天传了新体重，就覆盖掉刚才继承的旧体重)
        if (dto.getWeight() != null) {
            User user = userMapper.selectById(dto.getUserId());
            if (user == null) {
                return Result.error("用户不存在");
            }
            BigDecimal currentHeight = user.getHeight();

            // 如果 t_user 里没存身高，或者身高异常
            if (currentHeight == null || currentHeight.compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error("缺少身高数据，请先前往「个人中心」设置身高！");
            }

            record.setWeight(dto.getWeight());
            record.setHeight(currentHeight);

            // 计算 BMI
            BigDecimal heightInMeter = currentHeight.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = dto.getWeight().divide(heightInMeter.pow(2), 2, RoundingMode.HALF_UP);
            record.setBmi(bmi);
        }

        // 5. 处理三围数据 (如果有新值，就覆盖掉旧值)
        if (dto.getWaist() != null) record.setWaist(dto.getWaist());
        if (dto.getHip() != null) record.setHip(dto.getHip());
        if (dto.getChest() != null) record.setChest(dto.getChest());

        // 6. 处理经期起止时间
        if (Boolean.TRUE.equals(dto.getIsPeriodUpdate())) {
            // 如果是经期界面的专门操作，强制覆盖（允许用户传 null 来清空错误的打卡）
            record.setPeriodStartDate(dto.getPeriodStartDate());
            record.setPeriodEndDate(dto.getPeriodEndDate());
        } else {
            // 如果是从体重打卡界面来的，且带有经期数据，只做追加，不覆盖
            if (dto.getPeriodStartDate() != null) record.setPeriodStartDate(dto.getPeriodStartDate());
            if (dto.getPeriodEndDate() != null) record.setPeriodEndDate(dto.getPeriodEndDate());
        }

        // 7. 执行落库
        if (isUpdate) {
            bodyRecordMapper.updateById(record);
        } else {
            bodyRecordMapper.insert(record);
        }

        // 返回友好的提示信息
        if (dto.getWeight() != null) {
            return Result.success("身材打卡成功！当前BMI：" + record.getBmi());
        } else if (dto.getPeriodStartDate() != null || dto.getPeriodEndDate() != null) {
            return Result.success("生理期记录成功！");
        } else {
            return Result.success("数据保存成功！");
        }
    }

    // ... getRecordList 和 deleteRecord 保持不变 ...
    @Override
    public Result<List<UserBodyRecord>> getRecordList(Long userId) {
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
    public Result<String> deleteRecord(Long id) {
        if (id == null) {
            return Result.error("记录ID不能为空");
        }
        int rows = bodyRecordMapper.deleteById(id);
        return rows > 0 ? Result.success("记录删除成功") : Result.error("该记录不存在或已被删除");
    }
}