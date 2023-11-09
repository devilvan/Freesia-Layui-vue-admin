package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * @author Evad.Wu
 * @Description 全局配置信息表 数据传输对象
 * @date 2023-08-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "全局配置信息表 数据传输对象")
public class SysConfigDto extends BaseDto {
    @Schema(description = "参数名称")
    private String configName;
    @Schema(description = "参数键名")
    private String configKey;
    @Schema(description = "参数键值")
    private String configValue;
    @Schema(description = "系统内置（1-是 0-否）")
    private Boolean configType;
}
