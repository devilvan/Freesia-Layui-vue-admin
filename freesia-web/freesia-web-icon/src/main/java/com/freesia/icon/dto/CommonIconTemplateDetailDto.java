package com.freesia.icon.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 数据传输对象
 * @date 2025-04-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用图标模板表 数据传输对象")
public class CommonIconTemplateDetailDto extends BaseDto {
    @Schema(description = "通用图标模板头表ID")
    private Long headerId;
    @Schema(description = "父级ID")
    private Long parentId;
    @Schema(description = "图标ID")
    private Long iconId;
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
