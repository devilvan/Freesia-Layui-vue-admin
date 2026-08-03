package com.freesia.account.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 记账报表表 映射
 * @date 2026-02-25
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "ACCOUNT_REPORT")

@Entity
@Table(name = "ACCOUNT_REPORT")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "记账报表表 映射")
public class AccountReportPo extends BasePo implements Serializable {
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(1,024) COMMENT '备注'")
    private String remark;
    @Schema(description = "用户ID")
    @TableField(value = "USER_ID")
    @Column(name = "USER_ID", columnDefinition = "BIGINT(19) COMMENT '用户ID'")
    private Long userId;
    @Schema(description = "预算ID")
    @TableField(value = "BUDGET_ID")
    @Column(name = "BUDGET_ID", columnDefinition = "BIGINT(19) COMMENT '预算ID'")
    private Long budgetId;
    @Schema(description = "策略ID")
    @TableField(value = "STRATEGY_ID")
    @Column(name = "STRATEGY_ID", columnDefinition = "BIGINT(19) COMMENT '策略ID'")
    private Long strategyId;
    @Schema(description = "标题")
    @TableField(value = "TITLE")
    @Column(name = "TITLE", columnDefinition = "VARCHAR(64) COMMENT '标题'")
    private String title;
    @Schema(description = "预算类型（DAY-每日；WEEK-每周；MONTH-每月；YEAR-每年；CUSTOM-自定义）")
    @TableField(value = "BUDGET_TYPE")
    @Column(name = "BUDGET_TYPE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '预算类型（DAY-每日；WEEK-每周；MONTH-每月；YEAR-每年；CUSTOM-自定义）'")
    private String budgetType;
    @Schema(description = "预算金额")
    @TableField(value = "BUDGET_AMOUNT")
    @Column(name = "BUDGET_AMOUNT", columnDefinition = "DECIMAL(20) COMMENT '预算金额'")
    private BigDecimal budgetAmount;
    @Schema(description = "支出金额")
    @TableField(value = "OUTLAY")
    @Column(name = "OUTLAY", columnDefinition = "DECIMAL(20) COMMENT '支出金额'")
    private BigDecimal outlay;
    @Schema(description = "收入金额")
    @TableField(value = "INCOME_AMOUNT")
    @Column(name = "INCOME_AMOUNT", columnDefinition = "DECIMAL(20) COMMENT '收入金额'")
    private BigDecimal incomeAmount;
    @Schema(description = "报表时间")
    @TableField(value = "BILLING_TIME")
    @Column(name = "BILLING_TIME", columnDefinition = "DATETIME COMMENT '报表时间'")
    private Date billingTime;
    @Schema(description = "报表时间从")
    @TableField(value = "BILLING_TIME_FROM")
    @Column(name = "BILLING_TIME_FROM", columnDefinition = "DATETIME COMMENT '报表时间从'")
    private Date billingTimeFrom;
    @Schema(description = "报表时间到")
    @TableField(value = "BILLING_TIME_TO")
    @Column(name = "BILLING_TIME_TO", columnDefinition = "DATETIME COMMENT '报表时间到'")
    private Date billingTimeTo;
    @Schema(description = "是否完成重算（默认1，0-否；1-是）")
    @TableField(value = "RECALCULATE_FLAG")
    @Column(name = "RECALCULATE_FLAG", columnDefinition = "BIT(1) COMMENT '是否完成重算（默认1，0-否；1-是）'")
    private Boolean recalculateFlag;
}
