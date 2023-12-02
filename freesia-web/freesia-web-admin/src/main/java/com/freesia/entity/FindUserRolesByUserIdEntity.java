package com.freesia.entity;

import com.freesia.dto.SysRoleDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 根据用户ID查询【分配用户】加载数据 持久层传输类
 * @date 2023-11-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FindUserRolesByUserIdEntity extends BaseEntity {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "用户对应的角色信息")
    private Set<Long> selectedRoles;
}
