package com.xw.dto;

import lombok.Data;

/**
 * 管理员查询用户列表DTO
 * @author XW
 */
@Data
public class AdminUserQueryDTO {
    private Integer pageNum = 1;      // 页码，默认第1页
    private Integer pageSize = 10;    // 每页数量，默认10条
    private String keyword;           // 搜索关键词（手机号/用户名）
    private Integer status;           // 状态筛选：0-禁用 1-正常 null-全部
}
