package com.freesia.entity;

import com.freesia.dto.TreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description {@link sysMenuMapper#FindAllMenuTree} 持久层传输对象
 * @date 2023-09-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindAllMenuTreeEntity extends TreeDto<FindAllMenuTreeEntity> {
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "显示状态（0显示 1隐藏）")
    private String visible;
    @Schema(description = "菜单图标")
    private String icon;
}
