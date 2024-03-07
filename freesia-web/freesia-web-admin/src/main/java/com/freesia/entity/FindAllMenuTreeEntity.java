package com.freesia.entity;

import com.freesia.dto.TreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询所有菜单下拉树 持久层传输对象
 * {@link com.freesia.controller.SysMenuController#findAllMenuTree}
 * @date 2023-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindAllMenuTreeEntity extends TreeDto<FindAllMenuTreeEntity> {
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "显示状态（0-隐藏 1-显示）")
    private String visible;
    @Schema(description = "菜单图标")
    private String icon;
}
