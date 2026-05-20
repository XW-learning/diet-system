package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_check_in_detail")
public class CheckInDetail {
    @TableId
    private Long id;
    private Long checkInId;
    private Integer mealType;

    // 👇 修改这里的逻辑说明：普通打卡存 dishId，AI打卡存 aiRecordId
    private Long dishId; // AI打卡时，此字段为 null
    private Long aiRecordId; // 🌟 新增：AI识别打卡专属ID
    private String foodName; // 🌟 新增：食物名称（普通菜品存dish名，AI存识别出来的名字）

    private Integer calorie;
    private Integer type;
    private LocalDateTime createTime;

    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal fiber;
}