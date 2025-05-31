package com.freesia.icon.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.freesia.icon.entity.FindCommonIconEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板明细表 值对象
 * @date 2025-05-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用图标模板明细表 值对象")
public class CommonIconTemplateDetailVo extends BaseVo {
    @Schema(description = "通用图标模板头表ID")
    @JsonAlias(value = {"headerId"})
    private Long headerId;
    @Schema(description = "父级ID")
    @JsonAlias(value = {"parentId"})
    private Long parentId;
    @Schema(description = "图标ID")
    @JsonAlias(value = {"iconId"})
    private Long iconId;
    @Schema(description = "自定义图标名称")
    @JsonAlias(value = {"name"})
    private String name;
    @Schema(description = "自定义分组")
    @JsonAlias(value = {"grouping"})
    private String grouping;
    @Schema(description = "排序")
    @JsonAlias(value = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "节点类型（ICON_TREE_TYPE）")
    @JsonAlias(value = {"iconTreeType"})
    private String iconTreeType;
    @Schema(description = "批量图标数据")
    @JsonAlias(value = {"multipleIconList"})
    private List<FindCommonIconEntity> multipleIconList;
}
