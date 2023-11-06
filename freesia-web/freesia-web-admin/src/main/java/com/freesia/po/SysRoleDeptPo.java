package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 角色-部门关联表 映射
 * @date 2023-10-20
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_ROLE_DEPT")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_ROLE_DEPT")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "角色-部门关联表 映射")
public class SysRoleDeptPo extends RelationPo {
    @Serial
    private static final long serialVersionUID = 3381942492949819568L;
    @Id
    @Schema(description = "菜单ID")
    private Long menuId;
    @Id
    @Schema(description = "部门ID")
    private Long deptId;
}

@Data
@Embeddable
class SysRoleDeptPk implements Serializable {
    @Serial
    private static final long serialVersionUID = 5069682381557493963L;
    @Schema(description = "部门ID")
    @Column(name = "DEPT_ID")
    private Long deptId;
    @Schema(description = "角色ID")
    @Column(name = "ROLE_ID")
    private Long roleId;
}
