package com.xw.vo;

import com.xw.entity.Plan;
import lombok.Data;
import java.util.List;

/**
 * 饮食方案完整详情视图对象
 * @author XW
 */
@Data
public class PlanDetailVO {
    // 1. 方案的基础信息（名字、卡路里等）
    private Plan plan;

    // 2. 该方案下包含的所有菜品（按早中晚餐排好序）
    private List<MealVO> meals;
}