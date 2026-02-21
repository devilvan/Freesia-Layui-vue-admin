package com.freesia.account.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 记账账单表 数据传输对象
 * @date 2026-02-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "记账账单表 数据传输对象")
public class AccountBillingDto extends BaseDto {
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "预算ID")
    private Long budgetId;
    @Schema(description = "策略ID")
    private Long strategyId;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "预算类型（DAY-每日；WEEK-每周；MONTH-每月；YEAR-每年；CUSTOM-自定义）")
    private String budgetType;
    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;
    @Schema(description = "支出金额")
    private BigDecimal outlay;
    @Schema(description = "收入金额")
    private BigDecimal incomeAmount;
    @Schema(description = "账单时间")
    private Date billingTime;
    @Schema(description = "账单时间从")
    private Date billingTimeFrom;
    @Schema(description = "账单时间到")
    private Date billingTimeTo;
    @Schema(description = "是否重新计算（0-否；1-是）")
    private Boolean recalculateFlag;
}
