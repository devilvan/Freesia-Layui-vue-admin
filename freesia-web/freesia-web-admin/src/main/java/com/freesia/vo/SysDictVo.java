package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author Evad.Wu
 * @Description 字典信息表 值对象
 * @date 2023-09-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictVo extends BaseVo {
    @Schema(description = "字典键ID")
    private Long keyId;
    @Schema(description = "字典键名")
    private String keyName;
    @Schema(description = "字典键")
    @NotNull(message = "{not.null}")
    private String dictKey;
    @Schema(description = "字典值名")
    private String valueName;
    @Schema(description = "字典值")
    private String value;
    @Schema(description = "状态 0-启用 1-禁用")
    private String status;
}
