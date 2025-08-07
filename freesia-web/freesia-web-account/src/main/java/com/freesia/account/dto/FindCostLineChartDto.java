package com.freesia.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 折线图-根据时间查询 数据传输对象
 * @date 2025-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindCostLineChartDto extends AccountCostDto {
    @Schema(description = "年")
    private Integer year;
    @Schema(description = "月")
    private Integer month;
}
