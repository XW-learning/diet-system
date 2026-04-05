package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分享(饮食动态)实体类
 * @author XW
 */
@Data
@TableName("t_share")
public class Share {
    @TableId
    private Long id;

    private Long userId;

    private String content;

    // 隐私可见性：0公开 1私密
    private Integer privacy;

    // 🌟 新增的审核状态：0待审核 1通过 2驳回
    private Integer auditStatus;

    private Integer likeCount;

    private Integer commentCount;

    private LocalDateTime createTime;
}