package com.xw.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class FoodCalendarVO {

    // 日期为Key (例如 "2026-04-16")，值为当天的饮食概况
    private Map<String, DailyFoodVO> dailyData;

    @Data
    public static class DailyFoodVO {
        // 包含的餐次：["breakfast", "lunch", "dinner", "snack"]
        private List<String> meals;

        // 是否热量超标 (摄入 > 预算)
        private Boolean isOver;
    }
}