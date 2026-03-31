package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author XW
 */
@Data
@TableName("t_user_category")
public class UserCategory {
    @TableId
    private Long id;
    private String name;
    private Integer status;
}