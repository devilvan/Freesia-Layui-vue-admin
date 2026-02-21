package com.freesia.account.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.freesia.desensization.handler.NumberToStringSerializer;
import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 记账账单策略表 数据传输对象
 * @date 2026-02-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "记账账单策略表 数据传输对象")
public class AccountBillingStrategyDto extends BaseDto {
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "预算ID")
    private Long budgetId;
    @Schema(description = "预算类型（DAY-每日；WEEK-每周；MONTH-每月；YEAR-每年；CUSTOM-自定义）")
    private String budgetType;
    @Schema(description = "生成时间")
    private Date generateTime;
    @Schema(description = "下次生成时间")
    private Date nextGenerateTime;
    @Schema(description = "是否启用（0-否；1-是）")
    private Boolean enabled;
    @Schema(description = "开始周（1-星期一；7-星期日）")
    @JsonSerialize(using = NumberToStringSerializer.class)
    private Integer weekBegin;
    @Schema(description = "是否重新计算（0-否；1-是）")
    private Boolean recalculateFlag;
}
