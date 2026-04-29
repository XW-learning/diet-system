package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表 t_user
 *
 * @author XW
 */
@Data
@TableName("t_user")
public class User {
    
    /**
     * 用户ID，主键
     */
    @TableId
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
    /**
     * 手机号，唯一登录账号
     */
    private String phone;
    
    /**
     * 登录密码
     */
    private String password;
    
    /**
     * 用户昵称
     */
    private String username;
    
    /**
     * 性别：0-女，1-男
     */
    private Integer gender;
    
    /**
     * 用户状态：1-正常，0-禁用
     */
    private Integer status;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 身高，单位cm
     */
    private BigDecimal height;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 用户分类ID
     */
    private Long categoryId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}