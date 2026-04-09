package com.xw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("sort_order")
    private Integer sortOrder;
}
