package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 * 严格对应数据库 t_collection 表
 * @author XW
 */
@Data
@TableName("t_collection") // 🌟 纠正表名
public class UserPlanFavorite {
    @TableId
    private Long id;

    // 用户ID
    private Long userId;

    // 目标ID (如果 type 是 2，这里存的就是 plan_id)
    private Long targetId;

    // 类型：1分享 2方案
    // 🌟 注意：我们在代码里操作方案收藏时，要把这个值固定设为 2
    private Integer type;

    // 创建时间
    private LocalDateTime createTime;
}