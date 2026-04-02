package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户自定义方案实体类
 * @author XW
 */
@Data
@TableName("t_user_custom_plan")
public class UserCustomPlan {

    @TableId
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 基于哪个基础模板方案演变而来（方便后续做数据分析，也可为空）
     */
    private Long basePlanId;

    /**
     * 方案名称
     */
    private String name;

    /**
     * 替换后的总预估卡路里
     */
    private Integer totalCalorie;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}