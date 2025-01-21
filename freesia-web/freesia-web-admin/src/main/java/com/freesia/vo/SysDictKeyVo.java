package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 值对象
 * @date 2023-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典键信息表 值对象")
public class SysDictKeyVo extends BaseVo {
    @Schema(description = "字典键名")
    @JSONField(alternateNames = {"keyName"})
    @NotEmpty(message = "{not.null}")
    private String keyName;
    @Schema(description = "字典键")
    @JSONField(alternateNames = {"dictKey"})
    @NotEmpty(message = "{not.null}")
    private String dictKey;
    @Schema(description = "状态（0-禁用，1-启用）")
    @JSONField(alternateNames = {"status"})
    private String status;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
    @Schema(description = "根据字典键或字典键名查询键")
    @JSONField(alternateNames = {"keyNameOrDictKey"})
    private String keyNameOrDictKey;
}
