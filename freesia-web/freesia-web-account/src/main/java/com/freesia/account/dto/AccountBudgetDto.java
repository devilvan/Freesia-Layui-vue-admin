package com.freesia.account.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 数据传输对象
 * @date 2025-03-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "开销-预算表 数据传输对象")
public class AccountBudgetDto extends BaseDto {
    @Schema(description = "预算描述")
    private String budgetDesc;
    @Schema(description = "预算金额")
    private BigDecimal outlay;
    @Schema(description = "时间范围从")
    private Date durationFrom;
    @Schema(description = "时间范围到")
    private Date durationTo;
    @Schema(description = "预算类型（ACCOUNT_BUDGET_TYPE）")
    private String budgetType;
    @Schema(description = "备注")
    private String remark;
}
