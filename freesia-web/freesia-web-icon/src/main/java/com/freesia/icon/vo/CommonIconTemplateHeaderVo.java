package com.freesia.icon.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 值对象
 * @date 2025-04-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用图标模板头表 值对象")
public class CommonIconTemplateHeaderVo extends BaseVo {
    @Schema(description = "模板名称")
    @JSONField(alternateNames = {"name"})
    private String name;
    @Schema(description = "排序")
    @JSONField(alternateNames = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
}
