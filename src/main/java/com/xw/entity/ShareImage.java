package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_share_image")
public class ShareImage {
    @TableId
    private Long id;
    private Long shareId;
    private String imageUrl;
    private LocalDateTime createTime;
}