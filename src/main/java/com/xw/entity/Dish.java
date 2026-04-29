package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 * 对应数据库表 t_dish
 *
 * @author XW
 */
@Data
@TableName("t_dish")
public class Dish {
    
    /**
     * 菜品ID
     */
    @TableId
    private Long id;
    
    /**
     * 菜品名称
     */
    private String name;
    
    /**
     * 所属分类ID
     */
    private Integer categoryId;
    
    /**
     * 菜品描述
     */
    private String description;
    
    /**
     * 热量（基于参考重量）
     */
    private Integer calorie;
    
    /**
     * 烹饪方法
     */
    private String cookMethod;

    /**
     * 碳水化合物含量（基于参考重量）
     */
    private BigDecimal carbohydrate;
    
    /**
     * 蛋白质含量（基于参考重量）
     */
    private BigDecimal protein;
    
    /**
     * 脂肪含量（基于参考重量）
     */
    private BigDecimal fat;
    
    /**
     * 膳食纤维含量（基于参考重量）
     */
    private BigDecimal fiber;

    /**
     * 参考重量，例如 100.0
     */
    private BigDecimal refWeight;
    
    /**
     * 重量单位，例如 "克"
     */
    private String weightUnit;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}