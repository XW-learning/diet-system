package com.xw.entity.ai;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_ai_recognize")
public class AiRecognize {
    @TableId
    private Long id;
    private Long userId;
    private String imageUrl;
    // 存大模型返回的原始 JSON
    private String result;
    private Integer calorie;
    private LocalDateTime createTime;
}