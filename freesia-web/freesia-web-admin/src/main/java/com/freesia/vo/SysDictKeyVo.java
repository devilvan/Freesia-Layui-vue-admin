package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;

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
    @JsonAlias(value = {"keyName"})
    @NotEmpty(message = "{not.null}")
    private String keyName;
    @Schema(description = "字典键")
    @JsonAlias(value = {"dictKey"})
    @NotEmpty(message = "{not.null}")
    private String dictKey;
    @Schema(description = "状态（0-禁用，1-启用）")
    @JsonAlias(value = {"status"})
    private String status;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "根据字典键或字典键名查询键")
    @JsonAlias(value = {"keyNameOrDictKey"})
    private String keyNameOrDictKey;
}
