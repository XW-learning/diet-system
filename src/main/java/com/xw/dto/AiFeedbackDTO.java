package com.xw.dto;

import lombok.Data;

/**
 * @author XW
 */
@Data
public class AiFeedbackDTO {
    private Long recordId;      // 对应的识别记录 ID (t_ai_recognize 表的主键)
    private Long userId;
    private Integer correctCalorie; // 用户手动纠正的真实卡路里
}