package com.freesia.account.entity;

import com.freesia.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 饼图-查询各类型开销比例 实体类
 * @date 2025-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindCostTypeRatePieEntity extends BaseEntity {
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    private String costType;
    @Schema(description = "开销金额")
    private BigDecimal outlay;
}
