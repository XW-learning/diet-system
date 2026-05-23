package com.xw.dto.admin;

import lombok.Data;

@Data
public class AdminPlanQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private Long categoryId;
    private Integer status;
}
