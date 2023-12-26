package com.freesia.entity;

import com.freesia.dto.SysUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description 查询用户信息和已分配该角色的用户列表 持久层传输对象
 * {@link com.freesia.controller.SysRoleController#findPageUserByRoleId}
 * @date 2023-09-05
 */
@Data
public class FindPageUserByRoleIdEntity {
    @EqualsAndHashCode.Exclude
    private List<SysUserDto> userEntityList;
}
