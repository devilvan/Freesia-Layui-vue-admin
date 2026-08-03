package com.freesia.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 角色-菜单关联信息表-联合主键 映射
 * @date 2024-02-19
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色-菜单关联信息表-联合主键")
public class SysRoleMenuPk extends RelationPo implements Serializable {
    @Serial
    private static final long serialVersionUID = 5069682381557493963L;
    @Schema(description = "菜单ID")
    @Column(name = "MENU_ID")
    private Long menuId;
    @Schema(description = "角色ID")
    @Column(name = "ROLE_ID")
    private Long roleId;
}
