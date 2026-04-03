package com.xw.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
public class AdminVO {
    private Long id;
    private String username;
    private String role;
    private Integer status;
    private LocalDateTime createTime;
}