package com.freesia.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description Echarts饼图 实体类
 * @date 2025-01-20
 */
@Data
public class EchartPieOptionEntity {
    /**
     * 图表项标识
     */
    private Collection<String> legends;
    /**
     * 图表序列（K-V对）
     */
    private List<Series> series;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    @Data
    public static class Series {
        /**
         * 名称
         */
        private String name;
        /**
         * 值
         */
        private String value;
    }
}
