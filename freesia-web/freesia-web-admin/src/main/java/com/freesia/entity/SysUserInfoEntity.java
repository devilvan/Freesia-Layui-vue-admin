package com.freesia.entity;

import com.freesia.controller.SysLoginController;
import com.freesia.vo.SysUserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 用户信息 结果集
 * {@link SysLoginController#getInfo()}
 * @date 2023-08-24
 */
@Data
public class SysUserInfoEntity {
    @Schema(description = "用户信息")
    private SysUserEntity user;
    @Schema(description = "角色信息")
    private Set<String> roles;
    @Schema(description = "权限信息")
    private Set<String> permissions;
}
