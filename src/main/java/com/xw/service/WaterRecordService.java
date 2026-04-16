package com.xw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xw.dto.WaterAddDTO;
import com.xw.entity.UserWaterRecord;

public interface WaterRecordService extends IService<UserWaterRecord> {

    /**
     * 获取用户当天的饮水记录
     */
    UserWaterRecord getTodayRecord(Long userId);

    /**
     * 增加饮水量
     */
    void addWater(WaterAddDTO dto);
}