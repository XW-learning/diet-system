package com.xw.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员查看用户信息VO
 * @author XW
 */
@Data
@Schema(description = "管理员查看用户信息视图对象")
public class AdminUserVO {
    @Schema(description = "用户ID")
    private String id;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "性别：0-女 1-男")
    private Integer gender;

    @Schema(description = "状态：1-正常 0-禁用")
    private Integer status;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "分类ID")
    private String categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
