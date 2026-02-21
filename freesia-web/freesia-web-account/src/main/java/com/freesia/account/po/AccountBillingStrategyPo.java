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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 记账账单策略表 映射
 * @date 2026-02-21
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "ACCOUNT_BILLING_STRATEGY")

@Entity
@Table(name = "ACCOUNT_BILLING_STRATEGY")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "记账账单策略表 映射")
public class AccountBillingStrategyPo extends BasePo implements Serializable {
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
    @Schema(description = "预算类型（DAY-每日；WEEK-每周；MONTH-每月；YEAR-每年；CUSTOM-自定义）")
    @TableField(value = "BUDGET_TYPE")
    @Column(name = "BUDGET_TYPE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '预算类型（DAY-每日；WEEK-每周；MONTH-每月；YEAR-每年；CUSTOM-自定义）'")
    private String budgetType;
    @Schema(description = "生成时间")
    @TableField(value = "GENERATE_TIME")
    @Column(name = "GENERATE_TIME", columnDefinition = "DATETIME COMMENT '生成时间'")
    private Date generateTime;
    @Schema(description = "下次生成时间")
    @TableField(value = "NEXT_GENERATE_TIME")
    @Column(name = "NEXT_GENERATE_TIME", columnDefinition = "DATETIME COMMENT '下次生成时间'")
    private Date nextGenerateTime;
    @Schema(description = "是否启用（0-否；1-是）")
    @TableField(value = "ENABLED")
    @Column(name = "ENABLED", columnDefinition = "BIT(1) COMMENT '是否启用（0-否；1-是）'")
    private Boolean enabled;
    @Schema(description = "开始周（1-星期一；7-星期日）")
    @TableField(value = "WEEK_BEGIN")
    @Column(name = "WEEK_BEGIN", columnDefinition = "INT(10) COMMENT '开始周（1-星期一；7-星期日）'")
    private Integer weekBegin;
    @Schema(description = "是否重新计算（0-否；1-是）")
    @TableField(value = "RECALCULATE_FLAG")
    @Column(name = "RECALCULATE_FLAG", columnDefinition = "BIT(1) COMMENT '是否重新计算（0-否；1-是）'")
    private Boolean recalculateFlag;
}
