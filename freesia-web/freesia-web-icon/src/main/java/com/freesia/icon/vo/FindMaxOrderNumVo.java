package com.freesia.icon.vo;

import com.alibaba.fastjson.annotation.JSONField;
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
    @JSONField(alternateNames = {"headerId"})
    private Long headerId;
    @Schema(description = "自定义图标名称")
    @JSONField(alternateNames = {"name"})
    private String name;
    @Schema(description = "自定义分组")
    @JSONField(alternateNames = {"grouping"})
    private String grouping;
    @Schema(description = "节点类型（ICON_TREE_TYPE）")
    @JSONField(alternateNames = {"iconTreeType"})
    private String iconTreeType;
}
