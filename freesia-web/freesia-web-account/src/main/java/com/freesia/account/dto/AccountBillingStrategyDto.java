package com.freesia.account.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 记账账单策略表 数据传输对象
 * @date 2026-02-17
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
    @Schema(description = "生成状态（0-否；1-是）")
    private Integer generateFlag;
    @Schema(description = "是否启用（0-否；1-是）")
    private Integer enabled;
    @Schema(description = "开始周（0-星期日；1-星期一；6-星期六）")
    private Integer weekBegin;
}
