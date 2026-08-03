package com.freesia.account.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 映射
 * @date 2025-10-03
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "ACCOUNT_COST_USER_ALLOC")

@Entity
@Table(name = "ACCOUNT_COST_USER_ALLOC")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "费用分摊表 映射")
public class AccountCostUserAllocPo extends BasePo implements Serializable {
    @Schema(description = "记账ID")
    @TableField(value = "COST_ID")
    @Column(name = "COST_ID", columnDefinition = "BIGINT(19) COMMENT '记账ID'")
    private Long costId;
    @Schema(description = "用户ID")
    @TableField(value = "USER_ID")
    @Column(name = "USER_ID", columnDefinition = "BIGINT(19) COMMENT '用户ID'")
    private Long userId;
    @Schema(description = "分摊金额")
    @TableField(value = "AMOUNT")
    @Column(name = "AMOUNT", columnDefinition = "DECIMAL(20) COMMENT '分摊金额'")
    private BigDecimal amount;
    @Schema(description = "分摊时间")
    @TableField(value = "OPERATE_TIME")
    @Column(name = "OPERATE_TIME", columnDefinition = "DATETIME COMMENT '分摊时间'")
    private Date operateTime;
    @Schema(description = "是否分摊（0-否，1-是）")
    @TableField(value = "ALLOC_FLAG")
    @Column(name = "ALLOC_FLAG", columnDefinition = "BIT(1) COMMENT '是否分摊（0-否，1-是）'")
    private Boolean allocFlag;
}
