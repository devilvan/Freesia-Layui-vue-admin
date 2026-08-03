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
* @Description 全局配置信息表 值对象
* @date 2023-08-12
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "全局配置信息表 值对象")
public class SysConfigVo extends BaseVo {
    @Schema(description = "参数名称")
    @JsonAlias(value = {"configName"})
    @NotEmpty(message = "{not.null}")
    private String configName;
    @Schema(description = "参数键名")
    @JsonAlias(value = {"configKey"})
    @NotEmpty(message = "{not.null}")
    private String configKey;
    @Schema(description = "参数键值")
    @JsonAlias(value = {"configValue"})
    @NotEmpty(message = "{not.null}")
    private String configValue;
}
