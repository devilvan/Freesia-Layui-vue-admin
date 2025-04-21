package com.freesia.icon.entity;

import com.freesia.dto.TreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Bliss.Wu
 * @Description 查询通用图标模板明细的节点数据 结果集
 * @date 2025-04-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindTreeIconTreeTypeEntity extends TreeDto<FindTreeIconTreeTypeEntity> {
    @Schema(description = "通用图标模板头表ID")
    private Long headerId;
    @Schema(description = "自定义图标名称")
    private String name;
    @Schema(description = "自定义分组")
    private String grouping;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "节点类型（ICON_TREE_TYPE）")
    private String iconTreeType;
}
