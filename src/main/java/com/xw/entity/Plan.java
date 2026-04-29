package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 饮食方案实体类
 * 对应数据库表 t_diet_plan
 *
 * @author XW
 */
@Data
@TableName("t_diet_plan")
public class Plan {
    
    /**
     * 方案ID
     */
    @TableId
    private Long id;

    /**
     * 方案名称
     */
    private String name;

    /**
     * 适用人群分类ID，关联 t_user_category
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
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}