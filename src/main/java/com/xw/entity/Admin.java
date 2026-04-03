package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员实体类
 * @author XW
 */
@Data
@TableName("t_admin") // 映射数据库表名
public class Admin {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String role; // 超级管理员/普通管理员
    private Integer status; // 状态：1正常 0禁用
    private LocalDateTime createTime;
}