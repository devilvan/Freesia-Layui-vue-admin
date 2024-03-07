package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 字典值信息表 值对象
 * @date 2023-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典值信息表 值对象")
public class SysDictValueVo extends BaseVo {
    @Schema(description = "字典键ID")
    @JSONField(alternateNames = {"keyId"})
    private Long keyId;
    @Schema(description = "字典键名")
    @JSONField(alternateNames = {"dictKey"})
    private String dictKey;
    @Schema(description = "字典值名")
    @JSONField(alternateNames = {"valueName"})
    private String valueName;
    @Schema(description = "字典值")
    @JSONField(alternateNames = {"value"})
    private String value;
    @Schema(description = "内部排序值")
    @JSONField(alternateNames = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "是否默认（0-否，1-是）")
    @JSONField(alternateNames = {"isDefault"})
    private String isDefault;
    @Schema(description = "国际化展示编码")
    @JSONField(alternateNames = {"i18n"})
    private String i18n;
    @Schema(description = "前端展示样式（CSS）")
    @JSONField(alternateNames = {"cssStyle"})
    private String cssStyle;
    @Schema(description = "状态（0-禁用，1-启用）")
    @JSONField(alternateNames = {"status"})
    private String status;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
}
