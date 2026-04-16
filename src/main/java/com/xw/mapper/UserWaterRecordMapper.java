package com.xw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xw.entity.UserWaterRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserWaterRecordMapper extends BaseMapper<UserWaterRecord> {

    /**
     * 利用数据库的原子性进行累加，避免并发脏写
     */
    @Update("UPDATE user_water_record SET current_amount = current_amount + #{addAmount} WHERE id = #{id}")
    void addWaterAmount(@Param("id") Long id, @Param("addAmount") Integer addAmount);
}