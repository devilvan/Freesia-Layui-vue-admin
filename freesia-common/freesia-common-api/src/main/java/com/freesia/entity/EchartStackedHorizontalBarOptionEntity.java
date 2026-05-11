package com.freesia.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonProperty(value = "yAxis")
    private List<String> yAxis;
    /**
     * X轴值
     */
    private List<Series> series;

    /**
     * Y轴值
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Series {
        /**
         * 名称
         */
        private String name;
        /**
         * 值
         */
        private List<BigDecimal> value;
        /**
         * 堆叠组
         */
        private String stack;
    }

}
