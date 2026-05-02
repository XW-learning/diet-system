// filepath: xw/entity/Plan.java
package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_diet_plan")
public class Plan {
    @TableId
    private Long id;
    private String name;
    private Long categoryId;
    private Integer calorieMin;
    private Integer calorieMax;
    private String principle;
    private Integer status;
    private String coverImage;
    private Integer duration;
    private String weightLoss;
    private Integer usageCount;
    private String tags;
    private LocalDateTime createTime;
}