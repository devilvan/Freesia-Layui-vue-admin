package com.freesia.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description Echarts日历 实体类
 * @date 2025-01-27
 */
@Data
public class EchartCalendarOptionEntity {
    /**
     * 图表序列（K-V对），第一个元素为key，第二个元素为value
     */
    private List<List<String>> series;
    /**
     * 最大值
     */
    private BigDecimal maxValue;
    /**
     * 显示日期范围，例：['2024-01-01', '2025-01-01']
     */
    private String[] range;
}
