package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 敏感操作信息表 映射
 * @date 2023-08-14
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_SENSITIVE_LOG")

@Entity
@Table(name = "SYS_SENSITIVE_LOG")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "敏感操作信息表 映射")
public class SysSensitiveLogPo extends BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = 4677562836109161302L;
    @Schema(description = "操作人ID")
    @TableField(value = "OPERATOR_ID")
    @Column(name = "OPERATOR_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '操作人ID'")
    private Long operatorId;
    @Schema(description = "操作人姓名")
    @TableField(value = "OPERATOR_NAME")
    @Column(name = "OPERATOR_NAME", columnDefinition = "VARCHAR(32) COMMENT '操作人姓名'")
    private String operatorName;
    @Schema(description = "所属部门ID")
    @TableField(value = "DEPT_ID")
    @Column(name = "DEPT_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '所属部门ID'")
    private Long deptId;
    @Schema(description = "部门名称")
    @TableField(value = "DEPT_NAME")
    @Column(name = "DEPT_NAME", columnDefinition = "VARCHAR(32) COMMENT '部门名称'")
    private String deptName;
    @TableField(value = "METHOD_TYPE")
    @Column(name = "METHOD_TYPE", columnDefinition = "VARCHAR(16) COMMENT '请求类型'")
    @Schema(description = "请求类型")
    private String methodType;
    @Schema(description = "URL")
    @TableField(value = "URL")
    @Column(name = "URL", columnDefinition = "VARCHAR(256) COMMENT 'URL'")
    private String url;
    @Schema(description = "被操作用户ID")
    @TableField(value = "BE_OPERATED_ID")
    @Column(name = "BE_OPERATED_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '被操作用户ID'")
    private Long beOperatedId;
    @Schema(description = "被操作用户姓名")
    @TableField(value = "BE_OPERATED_NAME")
    @Column(name = "BE_OPERATED_NAME", columnDefinition = "VARCHAR(32) COMMENT '被操作用户姓名'")
    private String beOperatedName;
    @Schema(description = "IP地址")
    @TableField(value = "IP_ADDRESS")
    @Column(name = "IP_ADDRESS", columnDefinition = "VARCHAR(256) COMMENT 'IP地址'")
    private String ipAddress;
    @Schema(description = "操作地点")
    @TableField(value = "LOCATION")
    @Column(name = "LOCATION", columnDefinition = "VARCHAR(255) COMMENT '操作地点'")
    private String location;
    @Schema(description = "操作时间")
    @TableField(value = "OPERATE_TIME")
    @Column(name = "OPERATE_TIME", columnDefinition = "DATETIME NOT NULL COMMENT '操作时间'")
    private Date operateTime;
    @Schema(description = "浏览器")
    @TableField(value = "BROWSER")
    @Column(name = "BROWSER", columnDefinition = "VARCHAR(32) COMMENT '浏览器'")
    private String browser;
    @Schema(description = "操作系统")
    @TableField(value = "OS")
    @Column(name = "OS", columnDefinition = "VARCHAR(64) COMMENT '操作系统'")
    private String os;
    @Schema(description = "操作模块（见OPERATE_MODULE）")
    @TableField(value = "MODULE")
    @Column(name = "MODULE", columnDefinition = "VARCHAR(32) COMMENT '操作模块（见OPERATE_MODULE）'")
    private String module;
    @Schema(description = "操作子模块（见OPERATE_MODULE）")
    @TableField(value = "SUB_MODULE")
    @Column(name = "SUB_MODULE", columnDefinition = "VARCHAR(32) COMMENT '操作子模块（见OPERATE_MODULE）'")
    private String subModule;
    @Schema(description = "操作类型（见OPERATE_TYPE）")
    @TableField(value = "TYPE")
    @Column(name = "TYPE", columnDefinition = "VARCHAR(32) COMMENT '操作类型（见OPERATE_TYPE）'")
    private String type;
    @Schema(description = "操作结果")
    @TableField(value = "RESULT")
    @Column(name = "RESULT", columnDefinition = "VARCHAR(32) COMMENT '操作结果'")
    private String result;
    @Schema(description = "操作前的JSON，敏感字段要加密")
    @TableField(value = "CONTEXT_OLD")
    @Column(name = "CONTEXT_OLD", columnDefinition = "TEXT(65,535) COMMENT '操作前的JSON，敏感字段要加密'")
    private String contextOld;
    @Schema(description = "操作后的JSON，敏感字段要加密")
    @TableField(value = "CONTEXT")
    @Column(name = "CONTEXT", columnDefinition = "TEXT(65,535) COMMENT '操作后的JSON，敏感字段要加密'")
    private String context;
    @Schema(description = "操作标识（字段、单号等）")
    @TableField(value = "SIGN")
    @Column(name = "SIGN", columnDefinition = "VARCHAR(256) COMMENT '操作标识（字段、单号等）'")
    private String sign;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;
}
