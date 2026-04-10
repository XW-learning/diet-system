package com.xw.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员查看用户信息VO
 * @author XW
 */
@Data
public class AdminUserVO {
    private String id;
    private String phone;             // 手机号
    private String username;          // 用户名
    private Integer gender;           // 性别：0-女 1-男
    private Integer status;           // 状态：1-正常 0-禁用
    private String avatar;            // 头像
    private Integer age;              // 年龄
    private String email;             // 邮箱
    private String categoryId;          // 分类ID
    private String categoryName;      // 分类名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 注册时间
}
