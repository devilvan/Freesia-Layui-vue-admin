package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 字典信息表 数据传输对象
 * @date 2023-09-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictDto extends BaseDto {
    @Schema(description = "字典键名")
    private String keyName;
    @Schema(description = "字典键")
    private String dictKey;
    @Schema(description = "字典值名")
    private String valueName;
    @Schema(description = "字典值")
    private String value;
    @Schema(description = "状态（0-禁用，1-启用）")
    private String status;
}
