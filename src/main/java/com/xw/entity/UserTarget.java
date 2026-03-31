package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_user_target")
public class UserTarget {
    @TableId
    private Long id;
    private Long userId;
    private BigDecimal targetWeight;
    private LocalDate targetDate;
    private String goalType;
    private LocalDateTime createTime;
}