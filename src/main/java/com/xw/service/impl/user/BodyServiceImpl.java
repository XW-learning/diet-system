package com.xw.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xw.dto.user.BodyRecordDTO;
import com.xw.entity.user.User;
import com.xw.entity.user.UserBodyRecord;
import com.xw.exception.BusinessException;
import com.xw.mapper.user.UserBodyRecordMapper;
import com.xw.mapper.user.UserMapper;
import com.xw.service.user.BodyService;
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
    public String saveRecord(Long userId, BodyRecordDTO dto) {

        LocalDateTime targetTime = dto.getRecordTime() != null ? dto.getRecordTime() : LocalDateTime.now();
        LocalDate targetDate = targetTime.toLocalDate();

        LambdaQueryWrapper<UserBodyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBodyRecord::getUserId, userId)
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
            record.setUserId(userId);
            record.setRecordTime(targetTime);
            record.setCreateTime(LocalDateTime.now());

            LambdaQueryWrapper<UserBodyRecord> lastDataWrapper = new LambdaQueryWrapper<>();
            lastDataWrapper.eq(UserBodyRecord::getUserId, userId)
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

        if (dto.getWeight() != null) {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            BigDecimal currentHeight = user.getHeight();

            if (currentHeight == null || currentHeight.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("缺少身高数据，请先前往「个人中心」设置身高！");
            }

            record.setWeight(dto.getWeight());
            record.setHeight(currentHeight);

            BigDecimal heightInMeter = currentHeight.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = dto.getWeight().divide(heightInMeter.pow(2), 2, RoundingMode.HALF_UP);
            record.setBmi(bmi);
        }

        if (dto.getWaist() != null) record.setWaist(dto.getWaist());
        if (dto.getHip() != null) record.setHip(dto.getHip());
        if (dto.getChest() != null) record.setChest(dto.getChest());

        if (Boolean.TRUE.equals(dto.getIsPeriodUpdate())) {
            record.setPeriodStartDate(dto.getPeriodStartDate());
            record.setPeriodEndDate(dto.getPeriodEndDate());
        } else {
            if (dto.getPeriodStartDate() != null) record.setPeriodStartDate(dto.getPeriodStartDate());
            if (dto.getPeriodEndDate() != null) record.setPeriodEndDate(dto.getPeriodEndDate());
        }

        if (isUpdate) {
            bodyRecordMapper.updateById(record);
        } else {
            bodyRecordMapper.insert(record);
        }

        if (dto.getWeight() != null) {
            return "身材打卡成功！当前BMI：" + record.getBmi();
        } else if (dto.getPeriodStartDate() != null || dto.getPeriodEndDate() != null) {
            return "生理期记录成功！";
        } else {
            return "数据保存成功！";
        }
    }

    @Override
    public List<UserBodyRecord> getRecordList(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        LambdaQueryWrapper<UserBodyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBodyRecord::getUserId, userId)
                .orderByDesc(UserBodyRecord::getRecordTime)
                .last("LIMIT 7");
        return bodyRecordMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteRecord(Long userId, Long id) {
        if (id == null) {
            throw new BusinessException("记录ID不能为空");
        }

        UserBodyRecord record = bodyRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("该记录不存在或已被删除");
        }

        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("警告：您无权删除他人的记录！");
        }

        int rows = bodyRecordMapper.deleteById(id);
        if (rows <= 0) throw new BusinessException("该记录不存在或已被删除");
        return "记录删除成功";
    }
}
