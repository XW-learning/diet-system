package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户身体记录实体类
 * 对应数据库表 t_user_body_record
 *
 * @author XW
 */
@Data
@TableName("t_user_body_record")
public class UserBodyRecord {

    /**
     * 记录ID
     */
    @TableId
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 身高，单位cm
     */
    private BigDecimal height;
    
    /**
     * 体重，单位kg
     */
    private BigDecimal weight;
    
    /**
     * 体质指数BMI
     */
    private BigDecimal bmi;
    
    /**
     * 腰围，单位cm
     */
    private BigDecimal waist;
    
    /**
     * 臀围，单位cm
     */
    private BigDecimal hip;
    
    /**
     * 胸围，单位cm
     */
    private BigDecimal chest;
    
    /**
     * 经期开始日期
     */
    @TableField("period_start_date")
    private LocalDate periodStartDate;
    
    /**
     * 经期结束日期
     */
    @TableField("period_end_date")
    private LocalDate periodEndDate;
    
    /**
     * 记录时间
     */
    private LocalDateTime recordTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}