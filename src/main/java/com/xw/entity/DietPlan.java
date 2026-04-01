package com.xw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_diet_plan")
public class DietPlan {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 方案名称
     */
    private String name;

    /**
     * 适用人群分类ID
     */
    private Long categoryId;

    /**
     * 最低卡路里
     */
    private Integer calorieMin;

    /**
     * 最高卡路里
     */
    private Integer calorieMax;

    /**
     * 饮食原则说明
     */
    private String principle;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}