package com.xw.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class FatLossCalendarVO {

    // 日期为Key (例如 "2026-04-16")，值为当天的各类减脂数据
    private Map<String, DailyFatLossVO> dailyData;

    @Data
    public static class DailyFatLossVO {
        private Integer intakeCalorie;  // 饮食摄入 (橙)
        private Integer deficitCalorie; // 热量缺口 (绿)
        private BigDecimal weight;      // 体重 (紫)
        private Boolean hasWater;       // 是否有饮水记录 (蓝)
        private Boolean isPeriod;       // 是否经期 (红)
    }
}