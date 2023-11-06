package com.freesia.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description PO的父类
 * @date 2022-07-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "PO的父类")
public abstract class BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = -8496736827518774172L;

    /**
     * 巨坑，mybatis-plus的保存API出现问题，原因就是主键必须叫做'ID'，不能叫别的
     * 当字段被 {@link TableId} 修饰时，不需要再添加 {@link TableField}，否则启动会出现很多提示
     */
    @Id
    @Column(name = "ID", columnDefinition = "BIGINT(20) COMMENT '主键'")
    @TableId(type = IdType.ASSIGN_ID)
    @GenericGenerator(name = "snowFlakeIdGenerator", strategy = "com.freesia.component.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Schema(description = "主键ID")
    private Long id;
    @CreatedBy
    @Column(name = "CREATOR", updatable = false, columnDefinition = "VARCHAR(64) COMMENT '创建人'")
    @TableField(value = "CREATOR", fill = FieldFill.INSERT)
    @Schema(description = "创建人")
    private String creator;
    @CreatedDate
    @Column(name = "CREATE_TIME", columnDefinition = "DATETIME COMMENT '创建时间'", updatable = false)
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;
    @LastModifiedBy
    @Column(name = "MODIFIER", columnDefinition = "VARCHAR(64) COMMENT '修改人'")
    @TableField(value = "MODIFIER", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改人")
    private String modifier;
    @LastModifiedDate
    @Column(name = "MODIFY_TIME", columnDefinition = "DATETIME COMMENT '修改时间'")
    @TableField(value = "MODIFY_TIME", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private Date modifyTime;
    @Column(name = "LOGIC_DEL", columnDefinition = "TINYINT DEFAULT 0 COMMENT '逻辑删除'")
    @TableField(value = "LOGIC_DEL", fill = FieldFill.INSERT)
    @Schema(description = "逻辑删除")
    private Boolean logicDel;
    @Version
    @com.baomidou.mybatisplus.annotation.Version
    @Column(name = "REC_VER", columnDefinition = "BIGINT(20) DEFAULT 1 COMMENT '版本号'")
    @TableField(value = "REC_VER", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "版本号")
    private Long recVer;
}
