package com.freesia.account.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.RelationPo;
import com.freesia.po.SysUserPo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 开支-用户关联表 映射
 * @date 2025-02-26
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "ACCOUNT_COST_USER")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "ACCOUNT_COST_USER")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "开支-用户关联表 映射")
public class AccountCostUserPo extends RelationPo {
    @Serial
    private static final long serialVersionUID = 3381942492949819568L;
    @EmbeddedId
    @Schema(description = "开支-用户 联合主键")
    private AccountCostUserPk accountCostUserPk;
    @Schema(description = "开支-用户关系表对应的开支")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = AccountCostPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "COST_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private AccountCostPo accountCostPo;
    @Schema(description = "开支-用户关系表对应的用户")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysUserPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysUserPo sysUserPo;

    public AccountCostUserPo(AccountCostUserPk accountCostUserPk) {
        this.accountCostUserPk = accountCostUserPk;
    }
}

