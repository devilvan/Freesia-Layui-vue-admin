package com.freesia.icon.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 值对象
 * @date 2025-05-15
 */
@Data
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
