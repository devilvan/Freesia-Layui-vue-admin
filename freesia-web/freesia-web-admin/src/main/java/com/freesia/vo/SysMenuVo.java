package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
* @author Evad.Wu
* @Description 目录/菜单/按钮信息表 值对象
* @date 2023-08-12
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "目录-菜单-按钮信息表 值对象")
public class SysMenuVo extends BaseVo {
    @Schema(description = "菜单名称")
    @JSONField(alternateNames = {"menuName"})
    @NotEmpty(message = "{not.null}")
    private String menuName;
    @Schema(description = "父菜单ID")
    @JSONField(alternateNames = {"parentId"})
    private Long parentId;
    @Schema(description = "显示顺序")
    @JSONField(alternateNames = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "路由地址")
    @JSONField(alternateNames = {"path"})
    private String path;
    @Schema(description = "组件路径")
    @JSONField(alternateNames = {"component"})
    private String component;
    @Schema(description = "组件类型")
    @JSONField(alternateNames = {"componentType"})
    private String componentType;
    @Schema(description = "路由参数")
    @JSONField(alternateNames = {"queryParam"})
    private String queryParam;
    @Schema(description = "是否为外链（0-否 1-是）")
    @JSONField(alternateNames = {"isFrame"})
    private String isFrame;
    @Schema(description = "是否缓存（0-不缓存 1-缓存）")
    @JSONField(alternateNames = {"isCache"})
    private String isCache;
    @Schema(description = "菜单类型（见MENU_TYPE）")
    @JSONField(alternateNames = {"menuType"})
    @NotEmpty(message = "{not.null}")
    private String menuType;
    @Schema(description = "显示状态（0-隐藏 1-显示）")
    @JSONField(alternateNames = {"visible"})
    private String visible;
    @Schema(description = "菜单状态（0-停用 1-正常）")
    @JSONField(alternateNames = {"status"})
    private String status;
    @Schema(description = "权限标识")
    @JSONField(alternateNames = {"perms"})
    private String perms;
    @Schema(description = "菜单图标")
    @JSONField(alternateNames = {"icon"})
    private String icon;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
}
