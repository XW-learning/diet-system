package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_check_in")
public class CheckIn {
    @TableId
    private Long id;
    private Long userId;
    private LocalDate date;
    private Integer budgetCalorie; // 新增：当日预算
    private Integer totalCalorie;  // 饮食摄入总和
    private Integer burnCalorie;   // 新增：运动消耗总和
    private String remark;
    private LocalDateTime createTime;
}