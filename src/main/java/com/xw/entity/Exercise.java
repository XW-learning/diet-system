package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 运动项目实体类
 * 对应数据库表 t_exercise
 *
 * @author XW
 */
@Data
@TableName("t_exercise")
public class Exercise {
    
    /**
     * 运动项目ID
     */
    @TableId
    private Long id;
    
    /**
     * 运动项目名称
     */
    private String name;
    
    /**
     * 30分钟消耗的卡路里
     */
    private Integer caloriePerHalfHour;
    
    /**
     * 运动分类ID
     */
    private Integer categoryId;
    
    /**
     * 运动分类名称
     */
    private String category;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}