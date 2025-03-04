package com.freesia.account.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 值对象
 * @date 2025-03-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "开销-预算表 值对象")
public class AccountBudgetVo extends BaseVo {
    @Schema(description = "预算描述")
    @JSONField(alternateNames = {"budgetDesc"})
    private String budgetDesc;
    @Schema(description = "预算金额")
    @JSONField(alternateNames = {"outlay"})
    private BigDecimal outlay;
    @Schema(description = "时间范围从")
    @JSONField(alternateNames = {"durationFrom"})
    private Date durationFrom;
    @Schema(description = "时间范围到")
    @JSONField(alternateNames = {"durationTo"})
    private Date durationTo;
    @Schema(description = "预算日期类型（ACCOUNT_BUDGET_DURATION_TYPE）")
    @JSONField(alternateNames = {"budgetType"})
    private String budgetType;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
}
