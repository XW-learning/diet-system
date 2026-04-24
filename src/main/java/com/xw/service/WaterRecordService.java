package com.xw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xw.dto.WaterAddDTO;
import com.xw.entity.UserWaterRecord;

/**
 * 饮水记录服务接口
 * @author XW
 */
public interface WaterRecordService extends IService<UserWaterRecord> {

    /**
     * 获取今日饮水记录
     * 这个方法本来就设计得很好，直接传 userId
     */
    UserWaterRecord getTodayRecord(Long userId);

    /**
     * 🌟 核心安全修改：增加 userId 参数，防越权打卡
     */
    void addWater(Long userId, WaterAddDTO dto);
}