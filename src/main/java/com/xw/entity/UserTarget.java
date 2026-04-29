package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户目标实体类
 * 对应数据库表 t_user_target
 *
 * @author XW
 */
@Data
@TableName("t_user_target")
public class UserTarget {
    
    /**
     * 目标ID
     */
    @TableId
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 目标体重
     */
    private BigDecimal targetWeight;
    
    /**
     * 目标达成日期
     */
    private LocalDate targetDate;
    
    /**
     * 目标类型：减肥/健身
     */
    private String goalType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}