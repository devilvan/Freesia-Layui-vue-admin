package com.freesia.icon.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Bliss.Wu
 * @Description 查询自增排序号 值对象
 * @date 2025-04-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindMaxOrderNumVo extends BaseVo {
    @Schema(description = "通用图标模板头表ID")
    @JsonAlias(value = {"headerId"})
    private Long headerId;
    @Schema(description = "自定义图标名称")
    @JsonAlias(value = {"name"})
    private String name;
    @Schema(description = "自定义分组")
    @JsonAlias(value = {"grouping"})
    private String grouping;
    @Schema(description = "节点类型（ICON_TREE_TYPE）")
    @JsonAlias(value = {"iconTreeType"})
    private String iconTreeType;
}
