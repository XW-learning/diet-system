package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_user_body_record")
public class UserBodyRecord {

    @TableId
    private Long id;
    private Long userId;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bmi;
    private BigDecimal waist;
    private BigDecimal hip;
    private BigDecimal chest;
    @TableField("period_start_date")
    private LocalDate periodStartDate;
    @TableField("period_end_date")
    private LocalDate periodEndDate;
    private LocalDateTime recordTime;
    private LocalDateTime createTime;
}