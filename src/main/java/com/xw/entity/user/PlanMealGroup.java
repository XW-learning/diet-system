package com.xw.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_plan_meal_group")
public class PlanMealGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private String mealName;
    private Integer mealType;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
