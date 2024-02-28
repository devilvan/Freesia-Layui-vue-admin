package com.freesia.oss.po;

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
 * @Description OSS对象存储表 映射
 * @date 2024-02-28
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_OSS")

@Entity
@Table(name = "SYS_OSS")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "OSS对象存储表 映射")
public class SysOssPo extends BasePo implements Serializable {
    @Schema(description = "文件名")
    @TableField(value = "FILE_NAME")
    @Column(name = "FILE_NAME", columnDefinition = "VARCHAR(255) NOT NULL COMMENT '文件名'")
    private String fileName;
    @Schema(description = "原名")
    @TableField(value = "ORIGINAL_NAME")
    @Column(name = "ORIGINAL_NAME", columnDefinition = "VARCHAR(255) NOT NULL COMMENT '原名'")
    private String originalName;
    @Schema(description = "文件后缀名")
    @TableField(value = "FILE_SUFFIX")
    @Column(name = "FILE_SUFFIX", columnDefinition = "VARCHAR(10) NOT NULL COMMENT '文件后缀名'")
    private String fileSuffix;
    @Schema(description = "URL地址")
    @TableField(value = "URL")
    @Column(name = "URL", columnDefinition = "VARCHAR(500) NOT NULL COMMENT 'URL地址'")
    private String url;
    @Schema(description = "服务商")
    @TableField(value = "SERVICE")
    @Column(name = "SERVICE", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '服务商'")
    private String service;
}
