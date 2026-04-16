package com.xw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理员查询用户列表DTO
 * @author XW
 */
@Data
@Schema(description = "管理员查询用户列表请求参数")
public class AdminUserQueryDTO {
    @Schema(description = "页码，默认第1页", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量，默认10条", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "搜索关键词（手机号/用户名）", example = "张三")
    private String keyword;

    @Schema(description = "状态筛选：0-禁用 1-正常 null-全部", example = "1")
    private Integer status;
}
