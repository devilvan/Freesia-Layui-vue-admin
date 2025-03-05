package com.freesia.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 容量图-根据预算日期类型查询 值对象
 * @date 2025-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindBudgetCapacityVo extends AccountBudgetVo {
    /**
     * {@link com.freesia.account.constant.DateScope}
     */
    @Schema(description = "时间范围")
    private String dateScope;
    @Schema(description = "年")
    private Integer year;
    @Schema(description = "月")
    private Integer month;
}
