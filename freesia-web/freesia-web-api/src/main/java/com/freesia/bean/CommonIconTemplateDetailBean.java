package com.freesia.bean;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板明细表 数据传输对象
 * @date 2025-05-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用图标模板明细表 数据传输对象")
public class CommonIconTemplateDetailBean extends BaseDto {
    @Schema(description = "自定义分组")
    private String grouping;
    @Schema(description = "通用图标模板头表ID")
    private Long headerId;
    @Schema(description = "图标ID")
    private Long iconId;
    @Schema(description = "节点类型（ICON_TREE_TYPE）")
    private String iconTreeType;
    @Schema(description = "自定义图标名称")
    private String name;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "父级ID")
    private Long parentId;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "主键集合")
    private List<Long> idList;
}
