package com.xw.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 饮食打卡明细表
 * @author XW
 */
@Data
@TableName("t_check_in_detail")
public class CheckInDetail {

    @TableId
    private Long id;

    /**
     * 关联的打卡主表(t_check_in)的ID
     */
    private Long checkInId;

    /**
     * 餐次：1早餐 2午餐 3晚餐 4加餐
     */
    private Integer mealType;

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 该菜品的卡路里(快照保存，防止后续菜品库热量被修改导致历史打卡数据错乱)
     */
    private Integer calorie;

    /**
     * 打卡类型：1-按方案推荐打卡 2-自定义打卡
     */
    private Integer type;

    /**
     * 打卡时间
     */
    private LocalDateTime createTime;
}