package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 饮食方案实体类
 * 严格对应数据库 t_diet_plan 表
 * @author XW
 */
@Data
@TableName("t_diet_plan") // 🌟 纠正表名
public class Plan {
    @TableId
    private Long id;

    // 方案名称
    private String name;

    // 适用人群分类ID (外键，关联 t_user_category)
    private Long categoryId;

    // 最低卡路里
    private Integer calorieMin;

    // 最高卡路里
    private Integer calorieMax;

    // 饮食原则说明
    private String principle;

    // 状态：1启用 0禁用
    private Integer status;

    // 创建时间
    private LocalDateTime createTime;
}