package com.freesia.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 排名-按消费类型排名 值对象
 * @date 2025-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindRankByCostTypeVo extends AccountCostVo {
    @Schema(description = "日期类型（周、月）")
    private String dateScope;
}
