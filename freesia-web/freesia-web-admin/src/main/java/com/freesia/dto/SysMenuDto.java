package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * @author Evad.Wu
 * @Description 目录/菜单/按钮信息表 数据传输对象
 * @date 2023-08-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "目录-菜单-按钮信息表 数据传输对象")
public class SysMenuDto extends TreeDto<SysMenuDto> {
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "父菜单ID")
    private Long parentId;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "路由地址")
    private String path;
    @Schema(description = "组件路径")
    private String component;
    @Schema(description = "组件类型")
    private String componentType;
    @Schema(description = "路由参数")
    private String queryParam;
    @Schema(description = "是否为外链（0-是 1-否）")
    private String isFrame;
    @Schema(description = "是否缓存（0-缓存 1-不缓存）")
    private String isCache;
    @Schema(description = "菜单类型（见MENU_TYPE）")
    private String menuType;
    @Schema(description = "显示状态（0显示 1隐藏）")
    private String visible;
    @Schema(description = "菜单状态（0正常 1停用）")
    private String status;
    @Schema(description = "权限标识")
    private String perms;
    @Schema(description = "菜单图标")
    private String icon;
    @Schema(description = "备注")
    private String remark;
}
