package com.freesia.account.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 数据传输对象
 * @date 2025-03-05
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
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date durationFrom;
    @Schema(description = "时间范围到")
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date durationTo;
    @Schema(description = "预算类型（ACCOUNT_BUDGET_DURATION_TYPE）")
    private String budgetType;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "适用策略ID")
    private Long strategyId;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "租户名称")
    private String tenantName;
}
