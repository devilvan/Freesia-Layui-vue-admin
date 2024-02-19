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
 * @Description 角色-部门 联合主键
 * @date 2024-02-19
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色-部门 联合主键")
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
