package com.xw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_ex_category")
public class ExCategory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer sortOrder;
}