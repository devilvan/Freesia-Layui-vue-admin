package com.freesia.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 租户-用户关联信息表-联合主键 映射
 * @date 2024-02-05
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SysTenantUserPk implements Serializable {
    @Serial
    private static final long serialVersionUID = 5069682381557493963L;
    @Schema(description = "租户ID")
    @Column(name = "TENANT_ID")
    private Long tenantId;
    @Schema(description = "用户ID")
    @Column(name = "USER_ID")
    private Long userId;
}
