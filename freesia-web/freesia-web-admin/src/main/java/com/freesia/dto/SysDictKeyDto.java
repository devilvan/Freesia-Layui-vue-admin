package com.freesia.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 数据传输对象
 * @date 2023-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典键信息表 数据传输对象")
public class SysDictKeyDto extends BaseDto {
    @Schema(description = "字典键名")
    private String keyName;
    @Schema(description = "字典键")
    private String dictKey;
    @Schema(description = "状态（0-禁用，1-启用）")
    private String status;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "根据字典键或字典键名查询键")
    @JSONField(alternateNames = {"keyNameOrDictKey"})
    private String keyNameOrDictKey;
}
