package com.xw.dto;

import lombok.Data;

@Data
public class AdminShareQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Integer auditStatus;
    private Long userId;
}
