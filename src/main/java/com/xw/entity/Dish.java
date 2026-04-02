package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜品主表
 * @author XW
 */
@Data
@TableName("t_dish")
public class Dish {
    @TableId
    private Long id;
    private String name;
    private String description;
    private Integer calorie; // 卡路里(kcal)
    private String cookMethod; // 制作方法
    private LocalDateTime createTime;
}