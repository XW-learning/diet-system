package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_dish")
public class Dish {
    @TableId
    private Long id;
    private String name;
    private String description;
    private Integer calorie; // 原始热量（基于参考重量）
    private String cookMethod;

    // 宏量营养素（基于参考重量）
    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal fiber;

    // 🌟 新增字段
    private BigDecimal refWeight; // 参考重量，例如 100.0
    private String weightUnit;    // 单位，例如 "克"

    private LocalDateTime createTime;
}