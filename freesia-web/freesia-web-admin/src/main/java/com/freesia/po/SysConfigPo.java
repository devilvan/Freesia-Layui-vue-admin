package com.freesia.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 全局配置信息表 映射
 * @date 2023-08-12
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_CONFIG")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_CONFIG")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "全局配置信息表 映射")
public class SysConfigPo extends BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = -2304320596194275243L;
    @Schema(description = "参数名称")
    @TableField(value = "CONFIG_NAME")
    @Column(name = "CONFIG_NAME", columnDefinition = "VARCHAR(100) COMMENT '参数名称'")
    private String configName;
    @Schema(description = "参数键名")
    @TableField(value = "CONFIG_KEY")
    @Column(name = "CONFIG_KEY", columnDefinition = "VARCHAR(100) COMMENT '参数键名'")
    private String configKey;
    @Schema(description = "参数键值")
    @TableField(value = "CONFIG_VALUE")
    @Column(name = "CONFIG_VALUE", columnDefinition = "VARCHAR(500) COMMENT '参数键值'")
    private String configValue;
}
