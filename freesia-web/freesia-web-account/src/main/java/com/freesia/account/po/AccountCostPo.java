package com.freesia.account.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import com.freesia.po.SysUserPo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    @Column(name = "ICON", columnDefinition = "TEXT COMMENT '图标'")
    private String icon;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;
    @Schema(description = "用户ID")
    @TableField(value = "USER_ID")
    @Column(name = "USER_ID", columnDefinition = "BIGINT(20) COMMENT '用户ID'")
    private Long userId;
    @Schema(description = "开销对应的用户")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToMany(targetEntity = SysUserPo.class, cascade = {CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinTable(name = "ACCOUNT_COST_USER",
            joinColumns = {@JoinColumn(name = "COST_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    @Fetch(value = FetchMode.SUBSELECT)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<SysUserPo> sysUserPoSet;
}
