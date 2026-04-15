package com.xw.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户基础信息更新 DTO
 * @author XW
 */
@Data
public class UserUpdateDTO {
    // 必须有 ID 才能知道更新谁（目前阶段前端传，后续可以从 Token 拿）
    private Long id;

    // 以下字段前端想改哪个就传哪个，不传就是 null
    private String username;
    private String avatar;
    private Integer gender; // 0-女, 1-男
    private Integer age;
    private String phone;
    private String email;
    private BigDecimal height;
}