package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_user") // 对应数据库表名
public class User {
    @TableId
    private Long id;
    private String phone;     // 手机号（唯一登录账号）
    private String password;  // 加密存储的密码
    private String username;
    private Integer gender;   // 0-女, 1-男
    private Integer status;   // 1-正常, 0-禁用
    private LocalDateTime createTime;
}