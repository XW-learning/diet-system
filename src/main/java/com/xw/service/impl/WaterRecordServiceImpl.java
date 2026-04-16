package com.xw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xw.dto.WaterAddDTO;
import com.xw.entity.UserWaterRecord;
import com.xw.mapper.UserWaterRecordMapper;
import com.xw.service.WaterRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WaterRecordServiceImpl extends ServiceImpl<UserWaterRecordMapper, UserWaterRecord> implements WaterRecordService {

    @Override
    public UserWaterRecord getTodayRecord(Long userId) {
        LocalDate today = LocalDate.now();

        // 使用 MyBatis-Plus 的 lambdaQuery
        UserWaterRecord record = this.lambdaQuery()
                .eq(UserWaterRecord::getUserId, userId)
                .eq(UserWaterRecord::getRecordDate, today)
                .one();

        // 如果今天还没喝水，返回一个默认的 0 进度对象给前端，不马上存入数据库
        if (record == null) {
            record = new UserWaterRecord();
            record.setUserId(userId);
            record.setRecordDate(today);
            record.setCurrentAmount(0);
            record.setTargetAmount(2000); // 默认目标，后续可从 UserPreference 表读取
        }

        return record;
    }

    @Override
    public void addWater(WaterAddDTO dto) {
        LocalDate today = LocalDate.now();

        UserWaterRecord record = this.lambdaQuery()
                .eq(UserWaterRecord::getUserId, dto.getUserId())
                .eq(UserWaterRecord::getRecordDate, today)
                .one();

        if (record == null) {
            // 今天第一次喝水，直接调用 IService 提供的 save 方法
            UserWaterRecord newRecord = new UserWaterRecord();
            newRecord.setUserId(dto.getUserId());
            newRecord.setRecordDate(today);
            newRecord.setCurrentAmount(dto.getAddAmount());
            newRecord.setTargetAmount(2000);
            this.save(newRecord);
        } else {
            // 已经有记录了，调用 mapper 的自定义原子累加 SQL
            this.baseMapper.addWaterAmount(record.getId(), dto.getAddAmount());
        }
    }
}