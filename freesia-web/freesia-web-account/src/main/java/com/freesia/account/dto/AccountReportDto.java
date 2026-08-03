package com.freesia.account.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.persistence.Column;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表表 数据传输对象
 * @date 2026-02-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "记账报表表 数据传输对象")
public class AccountReportDto extends BaseDto {
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
    @Schema(description = "报表时间")
    @JsonFormat(pattern = Constants.YMD)
    private Date billingTime;
    @Schema(description = "报表时间从")
    @JsonFormat(pattern = Constants.YMD)
    private Date billingTimeFrom;
    @Schema(description = "报表时间到")
    @JsonFormat(pattern = Constants.YMD)
    private Date billingTimeTo;
    @Schema(description = "是否完成重算（默认1，0-否；1-是）")
    private Boolean recalculateFlag;
    @Schema(description = "ID集合")
    private List<Long> idList;
}
