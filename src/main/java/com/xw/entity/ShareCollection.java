package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XW
 */ // 1. 点赞实体
@Data
@TableName("t_share_collection")
public class ShareCollection {
    @TableId
    private Long id;
    private Long userId;
    private Long shareId;
    private LocalDateTime createTime;
}