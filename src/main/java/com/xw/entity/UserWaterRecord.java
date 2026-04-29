package com.xw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户饮水记录实体类
 * 对应数据库表 user_water_record
 *
 * @author XW
 */
@Data
@TableName("user_water_record")
public class UserWaterRecord {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 记录日期
     */
    private LocalDate recordDate;
    
    /**
     * 当前饮水量，单位毫升
     */
    private Integer currentAmount;
    
    /**
     * 目标饮水量，单位毫升
     */
    private Integer targetAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}