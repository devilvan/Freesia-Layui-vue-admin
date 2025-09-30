package com.freesia.account.entity;

import com.freesia.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 排名-按消费类型排名
 * @date 2025-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindRankByCostTypeEntity extends BaseEntity {
    @Schema(description = "开支类型")
    private String costType;
    @Schema(description = "开支描述")
    private String costDesc;
    @Schema(description = "图标URL")
    private String iconUrl;
    @Schema(description = "开销金额")
    private BigDecimal outlay;
    @Schema(description = "排名")
    private Integer rk;
    @Schema(description = "日期标识")
    private String dateSign;
    @Schema(description = "周起始时间")
    private String weekStart;
    @Schema(description = "周结束时间")
    private String weekEnd;
}
