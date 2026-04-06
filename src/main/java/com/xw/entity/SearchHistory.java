package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author XW
 */
@Data
@TableName("t_search_history")
public class SearchHistory {
    @TableId
    private Long id;
    private Long userId;
    private String keyword;
    private LocalDateTime createTime;
}