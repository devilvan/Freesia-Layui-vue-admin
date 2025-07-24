package com.freesia.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description Echarts-堆叠条形图 实体类
 * @date 2025-07-20
 */
@Data
public class EchartStackedHorizontalBarOptionEntity {
    /**
     * Y轴键
     */
    private List<String> yAxis;
    /**
     * X轴值
     */
    private List<Series> series;

    /**
     * Y轴值
     */
    @Data
    public static class Series {
        /**
         * 名称
         */
        private String name;
        /**
         * 值
         */
        private List<BigDecimal> value;
    }

}
