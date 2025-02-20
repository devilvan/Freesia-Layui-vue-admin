package com.freesia.account.entity;

import com.freesia.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 日历-查询近一年支出 实体类
 * @date 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindCostSumCalendarNearYearEntity extends BaseEntity {
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
    private String xAxis;
}
