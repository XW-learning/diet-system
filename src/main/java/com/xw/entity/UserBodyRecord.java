package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
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
    private LocalDateTime recordTime;
    private LocalDateTime createTime;
}