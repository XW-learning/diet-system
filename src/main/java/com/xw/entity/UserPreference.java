package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_user_preference")
public class UserPreference {
    @TableId
    private Long id;
    private Long userId;
    private String taste;
    private String dietType;
    private LocalDateTime createTime;
}