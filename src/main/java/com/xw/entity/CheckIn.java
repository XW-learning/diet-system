package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 打卡记录实体类
 * 对应数据库表 t_check_in
 *
 * @author XW
 */
@Data
@TableName("t_check_in")
public class CheckIn {
    
    /**
     * 打卡记录ID
     */
    @TableId
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 打卡日期
     */
    private LocalDate date;
    
    /**
     * 当日卡路里预算
     */
    private Integer budgetCalorie;
    
    /**
     * 饮食摄入总卡路里
     */
    private Integer totalCalorie;
    
    /**
     * 运动消耗总卡路里
     */
    private Integer burnCalorie;
    
    /**
     * 备注信息
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}