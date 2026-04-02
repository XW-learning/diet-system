package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_ai_recognize")
public class AiRecognize {
    @TableId
    private Long id;
    private Long userId;
    private String imageUrl;
    private String result; // 存大模型返回的原始 JSON
    private Integer calorie;
    private LocalDateTime createTime;
}