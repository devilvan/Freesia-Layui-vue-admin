package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 字典值信息表 数据传输对象
 * @date 2023-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典值信息表 数据传输对象")
public class SysDictValueDto extends BaseDto {
    @Schema(description = "字典键ID")
    private Long keyId;
    @Schema(description = "字典键名")
    private String dictKey;
    @Schema(description = "字典值名")
    private String valueName;
    @Schema(description = "字典值")
    private String value;
    @Schema(description = "内部排序值")
    private Integer orderNum;
    @Schema(description = "是否默认（0-否，1-是）")
    private String isDefault;
    @Schema(description = "国际化展示编码")
    private String i18n;
    @Schema(description = "前端展示样式（CSS）")
    private String cssStyle;
    @Schema(description = "状态（0-禁用，1-启用）")
    private String status;
    @Schema(description = "备注")
    private String remark;
}
