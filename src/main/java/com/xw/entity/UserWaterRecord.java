package com.xw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_water_record")
public class UserWaterRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private LocalDate recordDate;
    private Integer currentAmount;
    private Integer targetAmount;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}