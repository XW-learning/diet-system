package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_exercise")
public class Exercise {
    @TableId
    private Long id;
    private String name;
    private Integer caloriePerHalfHour; // 30分钟消耗卡路里
    // 🌟 新增字段
    private Integer categoryId;
    private String category;
    private LocalDateTime createTime;
}