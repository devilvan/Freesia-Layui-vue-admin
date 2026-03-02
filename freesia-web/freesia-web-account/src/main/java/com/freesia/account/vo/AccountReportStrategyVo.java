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
 * @Description 记账报表策略表 值对象
 * @date 2026-02-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "记账报表策略表 值对象")
public class AccountReportStrategyVo extends BaseVo {
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "用户ID")
    @JsonAlias(value = {"userId"})
    private Long userId;
    @Schema(description = "预算ID")
    @JsonAlias(value = {"budgetId"})
    private Long budgetId;
    @Schema(description = "预算类型（DAY-每日；WEEK-每周；MONTH-每月；YEAR-每年；CUSTOM-自定义）")
    @JsonAlias(value = {"budgetType"})
    private String budgetType;
    @Schema(description = "生成时间")
    @JsonAlias(value = {"generateTime"})
    private Date generateTime;
    @Schema(description = "下次生成时间")
    @JsonAlias(value = {"nextGenerateTime"})
    private Date nextGenerateTime;
    @Schema(description = "是否启用（0-否；1-是）")
    @JsonAlias(value = {"enabled"})
    private Boolean enabled;
    @Schema(description = "开始周（1-星期一；7-星期日）")
    @JsonAlias(value = {"weekBegin"})
    private Integer weekBegin;
    @Schema(description = "是否完成重算（默认1，0-否；1-是）")
    @JsonAlias(value = {"recalculateFlag"})
    private Boolean recalculateFlag;
}
