package com.freesia.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description
 * @date 2025-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindRankByCostTypeDto extends AccountCostDto {
    @Schema(description = "日期类型（周、月）")
    private String dateScope;
}
