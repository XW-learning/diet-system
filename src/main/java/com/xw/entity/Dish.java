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
    private Integer calorie;
    private String cookMethod;

    // 🌟 新增的宏量营养素字段
    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal fiber;

    private LocalDateTime createTime;
}