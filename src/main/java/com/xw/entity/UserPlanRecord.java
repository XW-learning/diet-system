package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_user_plan_record")
public class UserPlanRecord {
    @TableId
    private Long id;
    private Long userId;
    private Long planId;
    private Integer status; // 状态：1-当前正在执行，0-历史记录
    private LocalDateTime createTime;
}