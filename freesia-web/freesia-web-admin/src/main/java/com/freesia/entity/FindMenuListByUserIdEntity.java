package com.freesia.entity;

import com.freesia.dto.TreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 根据用户ID查询菜单列表 持久层传输对象
 * {@link com.freesia.controller.SysMenuController#findMenuListByUserId}
 * @date 2023-09-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindMenuListByUserIdEntity extends TreeDto<FindMenuListByUserIdEntity> {
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "菜单类型")
    private String menuType;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "菜单图标")
    private String icon;
    @Schema(description = "路由地址")
    private String path;
    @Schema(description = "组件路径")
    private String component;
    @Schema(description = "权限标识")
    private String perms;
    @Schema(description = "显示状态（0显示 1隐藏）")
    private String visible;
    @Schema(description = "状态（0-启用 1-禁用）")
    private String status;
    @Schema(description = "是否外链")
    private String isFrame;
    @Schema(description = "备注")
    private String remark;
}
