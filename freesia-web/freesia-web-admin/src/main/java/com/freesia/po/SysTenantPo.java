package com.freesia.po;

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

/**
 * @author Evad.Wu
 * @Description 租户信息表 映射
 * @date 2024-01-31
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_TENANT")

@Entity
@Table(name = "SYS_TENANT")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "租户信息表 映射")
public class SysTenantPo extends BasePo implements Serializable {
    @Schema(description = "租户名称")
    @TableField(value = "NAME")
    @Column(name = "NAME", columnDefinition = "VARCHAR(128) NOT NULL COMMENT '租户名称'")
    private String name;
    @Schema(description = "租户备注")
    @TableField(value = "TYPE")
    @Column(name = "TYPE", columnDefinition = "VARCHAR(64) COMMENT '租户备注'")
    private String type;
    @Schema(description = "租户状态")
    @TableField(value = "STATUS")
    @Column(name = "STATUS", columnDefinition = "VARCHAR(16) COMMENT '租户状态'")
    private String status;
    @Schema(description = "租户备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(256) COMMENT '租户备注'")
    private String remark;
    @Schema(description = "联系人姓名")
    @TableField(value = "CONTACT_NAME")
    @Column(name = "CONTACT_NAME", columnDefinition = "VARCHAR(128) COMMENT '联系人姓名'")
    private String contactName;
    @Schema(description = "联系人电话")
    @TableField(value = "CONTACT_TEL")
    @Column(name = "CONTACT_TEL", columnDefinition = "VARCHAR(64) COMMENT '联系人电话'")
    private String contactTel;
    @Schema(description = "联系人邮箱")
    @TableField(value = "CONTACT_EMAIL")
    @Column(name = "CONTACT_EMAIL", columnDefinition = "VARCHAR(64) COMMENT '联系人邮箱'")
    private String contactEmail;
    @Schema(description = "租户地址")
    @TableField(value = "ADDRESS")
    @Column(name = "ADDRESS", columnDefinition = "VARCHAR(256) COMMENT '租户地址'")
    private String address;
    @Schema(description = "营业时间（从）")
    @TableField(value = "BUSINESS_HOURS_FROM")
    @Column(name = "BUSINESS_HOURS_FROM", columnDefinition = "DATETIME COMMENT '营业时间（从）'")
    private Date businessHoursFrom;
}
