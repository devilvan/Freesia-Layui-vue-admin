package com.freesia.account.po;

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
 * @Description 开支-用户关联信息表-联合主键 映射
 * @date 2025-02-26
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "开支-用户关联信息表-联合主键 映射")
public class AccountCostUserPk implements Serializable {
    @Serial
    private static final long serialVersionUID = 8925392164957371321L;
    @Schema(description = "开支ID")
    @Column(name = "COST_ID")
    private Long costId;
    @Schema(description = "用户ID")
    @Column(name = "USER_ID")
    private Long userId;
}
