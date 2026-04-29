package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分享动态实体类
 * 对应数据库表 t_share
 *
 * @author XW
 */
@Data
@TableName("t_share")
public class Share {
    
    /**
     * 分享ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分享内容
     */
    private String content;

    /**
     * 隐私可见性：0-公开，1-私密
     */
    private Integer privacy;

    /**
     * 审核状态：0-待审核，1-通过，2-驳回
     */
    private Integer auditStatus;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 收藏数
     */
    private Integer collectionCount;

    /**
     * 转发分享数
     */
    private Integer shareCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}