package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 租户-用户关联信息表 映射
 * @date 2024-02-04
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_TENANT_USER")

@Entity
@Table(name = "SYS_TENANT_USER")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "租户-用户关联信息表 映射")
public class SysTenantUserPo extends RelationPo implements Serializable {
    @Serial
    private static final long serialVersionUID = 7783181614120038158L;
    @EmbeddedId
    @Schema(description = "租户-用户关联 联合主键")
    private SysTenantUserPk sysTenantUserPk;

    @Schema(description = "租户-用户关系表对应的租户")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysTenantPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "TENANT_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysTenantPo sysTenantPo;
    @Schema(description = "租户-用户关系表对应的用户")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysUserPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysUserPo sysUserPo;

    public SysTenantUserPo(SysTenantUserPk sysTenantUserPk) {
        this.sysTenantUserPk = sysTenantUserPk;
    }

    /**
     * @author Evad.Wu
     * @Description 租户-用户关联信息表-联合主键 映射
     * @date 2024-02-05
     */
    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SysTenantUserPk implements Serializable {
        @Serial
        private static final long serialVersionUID = 5069682381557493963L;
        @Schema(description = "租户ID")
        @Column(name = "TENANT_ID")
        private Long tenantId;
        @Schema(description = "用户ID")
        @Column(name = "USER_ID")
        private Long userId;
    }
}
