package com.xw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@Schema(description = "管理员信息视图对象")
public class AdminVO {
    @Schema(description = "管理员ID")
    private String id;

    @Schema(description = "管理员用户名")
    private String username;

    @Schema(description = "角色类型")
    private String role;

    @Schema(description = "状态：1-正常 0-禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
