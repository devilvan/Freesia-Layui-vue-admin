package com.freesia.icon.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 值对象
 * @date 2025-05-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用图标模板头表 值对象")
public class CommonIconTemplateHeaderVo extends BaseVo {
    @Schema(description = "模板名称")
    @JsonAlias(value = {"name"})
    private String name;
    @Schema(description = "排序")
    @JsonAlias(value = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "所属用户ID")
    @JsonAlias(value = {"userId"})
    private Long userId;
    @Schema(description = "默认标识")
    @JsonAlias(value = {"defaultFlag"})
    private Boolean defaultFlag;
}
