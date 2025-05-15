package com.freesia.icon.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.icon.entity.FindCommonIconEntity;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 值对象
 * @date 2025-04-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用图标模板表 值对象")
public class CommonIconTemplateDetailVo extends BaseVo {
    @Schema(description = "通用图标模板头表ID")
    @JSONField(alternateNames = {"headerId"})
    private Long headerId;
    @Schema(description = "父级ID")
    @JSONField(alternateNames = {"parentId"})
    private Long parentId;
    @Schema(description = "图标ID")
    @JSONField(alternateNames = {"iconId"})
    private Long iconId;
    @Schema(description = "自定义图标名称")
    @JSONField(alternateNames = {"name"})
    private String name;
    @Schema(description = "自定义分组")
    @JSONField(alternateNames = {"grouping"})
    private String grouping;
    @Schema(description = "排序")
    @JSONField(alternateNames = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
    @Schema(description = "节点类型（ICON_TREE_TYPE）")
    @JSONField(alternateNames = {"iconTreeType"})
    private String iconTreeType;
    @Schema(description = "批量图标数据")
    @JSONField(alternateNames = {"multipleIconList"})
    private List<FindCommonIconEntity> multipleIconList;
    /**
     * URL
     */
    private String url;
}
