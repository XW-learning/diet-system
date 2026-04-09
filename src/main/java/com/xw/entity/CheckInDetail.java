package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_check_in_detail")
public class CheckInDetail {
    @TableId
    private Long id;
    private Long checkInId;
    private Integer mealType;
    private Long dishId;
    private Integer calorie;
    private Integer type;
    private LocalDateTime createTime;

    // 🌟 新增：记录本次摄入的宏量营养素
    private BigDecimal carbohydrate; // 碳水
    private BigDecimal protein;      // 蛋白质
    private BigDecimal fat;          // 脂肪
    private BigDecimal fiber;        // 纤维
}