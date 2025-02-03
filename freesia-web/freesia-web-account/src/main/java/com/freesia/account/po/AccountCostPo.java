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
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 开销表 映射
 * @date 2024-12-23
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
    @Serial
    private static final long serialVersionUID = -1468239437494588583L;
    @Schema(description = "开销描述")
    @TableField(value = "COST_DESC")
    @Column(name = "COST_DESC", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '开销描述'")
    private String costDesc;
    @Schema(description = "开销金额")
    @TableField(value = "OUTLAY")
    @Column(name = "OUTLAY", columnDefinition = "DECIMAL(18) NOT NULL COMMENT '开销金额'")
    private BigDecimal outlay;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    @TableField(value = "COST_TYPE")
    @Column(name = "COST_TYPE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '开销类型（ACCOUNT_COST_TYPE）'")
    private String costType;
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
