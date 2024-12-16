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
 * @Description 开销表 映射
 * @date 2024-12-14
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "ACCOUNT_COST")

@Entity
@Table(name = "ACCOUNT_COST")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "开销表 映射")
public class AccountCostPo extends BasePo implements Serializable {
    @Schema(description = "开销描述")
    @TableField(value = "DESC")
    @Column(name = "DESC", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '开销描述'")
    private String desc;
    @Schema(description = "开销金额")
    @TableField(value = "AMOUNT")
    @Column(name = "AMOUNT", columnDefinition = "DECIMAL(18) NOT NULL COMMENT '开销金额'")
    private BigDecimal amount;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    @TableField(value = "TYPE")
    @Column(name = "TYPE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '开销类型（ACCOUNT_COST_TYPE）'")
    private String type;
    @Schema(description = "开销标识（支出、收入）")
    @TableField(value = "PAYMENT_SIGN")
    @Column(name = "PAYMENT_SIGN", columnDefinition = "VARCHAR(16) NOT NULL COMMENT '开销标识（支出、收入）'")
    private String paymentSign;
    @Schema(description = "时间")
    @TableField(value = "PAYMENT_TIME")
    @Column(name = "PAYMENT_TIME", columnDefinition = "DATETIME COMMENT '时间'")
    private Date paymentTime;
    @Schema(description = "图标")
    @TableField(value = "ICON")
    @Column(name = "ICON", columnDefinition = "VARCHAR(32) COMMENT '图标'")
    private String icon;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;
}
