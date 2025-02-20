package com.freesia.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * @author Evad.Wu
 * @Description 折线图-根据时间查询 值对象
 * @date 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindCostLineChartVo extends AccountCostVo {
    /**
     * 时间范围 {@link com.freesia.account.constant.DateScope}
     */
    @Schema(description = "时间范围")
    @NotEmpty(message = "时间范围不能为空")
    private String dateScope;
    /**
     *
     */
    @Schema(description = "时间值（月/年）")
    private String dateValue;
}
