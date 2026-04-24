package com.xw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.dto.WaterAddDTO;
import com.xw.entity.UserWaterRecord;
import com.xw.mapper.UserWaterRecordMapper;
import com.xw.service.WaterRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * @author XW
 */
@Service
public class WaterRecordServiceImpl extends ServiceImpl<UserWaterRecordMapper, UserWaterRecord> implements WaterRecordService {

    @Override
    public UserWaterRecord getTodayRecord(Long userId) {
        // 这个方法本身很安全，只要确保传入的 userId 是从 ThreadLocal 拿的即可
        LocalDate today = LocalDate.now();

        UserWaterRecord record = this.lambdaQuery()
                .eq(UserWaterRecord::getUserId, userId)
                .eq(UserWaterRecord::getRecordDate, today)
                .one();

        if (record == null) {
            record = new UserWaterRecord();
            record.setUserId(userId);
            record.setRecordDate(today);
            record.setCurrentAmount(0);
            record.setTargetAmount(2000);
        }

        return record;
    }

    @Override
    public void addWater(Long userId, WaterAddDTO dto) { // 🌟 接收安全 userId
        LocalDate today = LocalDate.now();

        // 1. 🌟 使用传入的安全 userId 替换掉 dto.getUserId()
        UserWaterRecord record = this.lambdaQuery()
                .eq(UserWaterRecord::getUserId, userId)
                .eq(UserWaterRecord::getRecordDate, today)
                .one();

        if (record == null) {
            UserWaterRecord newRecord = new UserWaterRecord();
            newRecord.setUserId(userId); // 🌟 强制绑定当前登录人的 ID
            newRecord.setRecordDate(today);
            newRecord.setCurrentAmount(dto.getAddAmount());
            newRecord.setTargetAmount(2000);
            this.save(newRecord);
        } else {
            this.baseMapper.addWaterAmount(record.getId(), dto.getAddAmount());
        }
    }
}