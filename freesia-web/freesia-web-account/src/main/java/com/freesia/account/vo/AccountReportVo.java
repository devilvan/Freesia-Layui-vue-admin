package com.freesia.account.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 记账报表表 值对象
 * @date 2026-02-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "记账报表表 值对象")
public class AccountReportVo extends BaseVo {
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "用户ID")
    @JsonAlias(value = {"userId"})
    private Long userId;
    @Schema(description = "预算ID")
    @JsonAlias(value = {"budgetId"})
    private Long budgetId;
    @Schema(description = "策略ID")
    @JsonAlias(value = {"strategyId"})
    private Long strategyId;
    @Schema(description = "标题")
    @JsonAlias(value = {"title"})
    private String title;
    @Schema(description = "预算类型（DAY-每日；WEEK-每周；MONTH-每月；YEAR-每年；CUSTOM-自定义）")
    @JsonAlias(value = {"budgetType"})
    private String budgetType;
    @Schema(description = "预算金额")
    @JsonAlias(value = {"budgetAmount"})
    private BigDecimal budgetAmount;
    @Schema(description = "支出金额")
    @JsonAlias(value = {"outlay"})
    private BigDecimal outlay;
    @Schema(description = "收入金额")
    @JsonAlias(value = {"incomeAmount"})
    private BigDecimal incomeAmount;
    @Schema(description = "账单时间")
    @JsonAlias(value = {"billingTime"})
    private Date billingTime;
    @Schema(description = "账单时间从")
    @JsonAlias(value = {"billingTimeFrom"})
    private Date billingTimeFrom;
    @Schema(description = "账单时间到")
    @JsonAlias(value = {"billingTimeTo"})
    private Date billingTimeTo;
    @Schema(description = "是否重新计算（0-否；1-是）")
    @JsonAlias(value = {"recalculateFlag"})
    private Boolean recalculateFlag;
}
