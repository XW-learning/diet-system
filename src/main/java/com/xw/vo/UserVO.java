package com.xw.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author XW
 */
@Data
public class UserVO {
    // 基础信息 (来自 t_user)
    private String id;
    private String phone;
    private String username;
    private String avatar;
    private Integer gender; // 0-女, 1-男
    private Integer age;
    private String email;
    private String categoryName; // 人群分类名称 (如：减肥人群) [cite: 93]

    // 身材数据 (来自 t_user_body_record)
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bmi;
    private BigDecimal waist;
    private BigDecimal hip;

    // 目标数据 (来自 t_user_target)
    private BigDecimal targetWeight;
    private LocalDate targetDate;
    private String goalType; // 目标类型：减肥/健身

    // 偏好数据 (来自 t_user_preference)
    private String taste;     // 口味偏好 [cite: 31]
    private String dietType;  // 饮食类型 [cite: 31]
}