// filepath: xw/entity/Dish.java
package com.xw.entity.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_dish")
public class Dish {
    @TableId
    private Long id;
    private String name;
    private Integer categoryId;
    private String description;
    private Integer calorie;
    private String cookMethod;
    private BigDecimal carbohydrate;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal fiber;
    private BigDecimal refWeight;
    private String weightUnit;
    private String imageUrl;
    private LocalDateTime createTime;
}