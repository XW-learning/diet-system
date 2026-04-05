package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_share_like")
public class ShareLike {
    @TableId
    private Long id;
    private Long userId;
    private Long shareId;
    private LocalDateTime createTime;
}