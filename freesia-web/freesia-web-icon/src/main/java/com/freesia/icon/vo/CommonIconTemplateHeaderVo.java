package com.freesia.icon.vo;

import com.alibaba.fastjson.annotation.JSONField;
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
    @JSONField(alternateNames = {"name"})
    private String name;
    @Schema(description = "排序")
    @JSONField(alternateNames = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
    @Schema(description = "所属用户ID")
    @JSONField(alternateNames = {"userId"})
    private Long userId;
    @Schema(description = "默认标识")
    @JSONField(alternateNames = {"defaultFlag"})
    private Boolean defaultFlag;
}
