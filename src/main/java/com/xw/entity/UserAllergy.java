package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户过敏记录实体类
 * @author XW
 */
@Data
@TableName("t_user_allergy")
public class UserAllergy {
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 过敏原材料ID
     */
    private Long materialId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}