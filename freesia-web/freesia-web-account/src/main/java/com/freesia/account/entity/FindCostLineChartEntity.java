package com.freesia.account.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freesia.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 折线图-根据时间查询 实体类
 * @date 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindCostLineChartEntity extends BaseEntity {
    /**
     * 开销金额
     */
    @Schema(description = "开销金额")
    private BigDecimal outlay;
    /**
     * 开销时间
     */
    @Schema(description = "开销时间")
    private String paymentTime;
    /**
     * x轴描述
     */
    @Schema(description = "x轴描述")
    @JsonProperty(value = "xAxis")
    private String xAxis;
}
