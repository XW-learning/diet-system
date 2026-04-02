package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 饮食打卡统计表 (用于记录连续打卡天数和月度达标率)
 * @author XW
 */
@Data
@TableName("t_check_in_stat")
public class CheckInStat {

    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 连续打卡天数
     */
    private Integer continuousDays;

    /**
     * 本月打卡率(%)，例如 85.5 代表 85.5%
     */
    private BigDecimal monthRate;

    /**
     * 最后一次统计数据的更新时间
     */
    private LocalDateTime updateTime;
}