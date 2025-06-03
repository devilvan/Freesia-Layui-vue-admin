package com.freesia.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description Echarts折线图 实体类
 * @date 2025-01-24
 */
@Data
public class EchartLineOptionEntity {
    /**
     * X轴描述
     */
    @JsonProperty(value = "xAxis")
    private List<String> xAxis;
    /**
     * 图表序列（K-V对）
     */
    private List<Series> series;

    @Data
    public static class Series {
        /**
         * 数据
         */
        private BigDecimal[] data;
    }
}
