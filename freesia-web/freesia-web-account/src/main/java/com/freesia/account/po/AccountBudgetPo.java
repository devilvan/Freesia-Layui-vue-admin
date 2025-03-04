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

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 映射
 * @date 2025-03-04
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "ACCOUNT_BUDGET")

@Entity
@Table(name = "ACCOUNT_BUDGET")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "开销-预算表 映射")
public class AccountBudgetPo extends BasePo implements Serializable {
    @Schema(description = "预算描述")
    @TableField(value = "BUDGET_DESC")
    @Column(name = "BUDGET_DESC", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '预算描述'")
    private String budgetDesc;
    @Schema(description = "预算金额")
    @TableField(value = "OUTLAY")
    @Column(name = "OUTLAY", columnDefinition = "DECIMAL(18) NOT NULL COMMENT '预算金额'")
    private BigDecimal outlay;
    @Schema(description = "时间范围从")
    @TableField(value = "DURATION_FROM")
    @Column(name = "DURATION_FROM", columnDefinition = "DATETIME NOT NULL COMMENT '时间范围从'")
    private Date durationFrom;
    @Schema(description = "时间范围到")
    @TableField(value = "DURATION_TO")
    @Column(name = "DURATION_TO", columnDefinition = "DATETIME NOT NULL COMMENT '时间范围到'")
    private Date durationTo;
    @Schema(description = "预算日期类型（ACCOUNT_BUDGET_DURATION_TYPE）")
    @TableField(value = "BUDGET_TYPE")
    @Column(name = "BUDGET_TYPE", columnDefinition = "VARCHAR(32) COMMENT '预算日期类型（ACCOUNT_BUDGET_DURATION_TYPE）'")
    private String budgetType;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;
}
