package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 对应数据库表 t_admin
 *
 * @author XW
 */
@Data
@TableName("t_admin")
public class Admin {
    
    /**
     * 管理员ID
     */
    @TableId
    private Long id;
    
    /**
     * 管理员用户名
     */
    private String username;
    
    /**
     * 管理员密码
     */
    private String password;
    
    /**
     * 角色：超级管理员/普通管理员
     */
    private String role;
    
    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}