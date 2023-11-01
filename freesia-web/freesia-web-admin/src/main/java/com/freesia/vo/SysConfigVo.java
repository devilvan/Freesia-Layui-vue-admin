package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @author Evad.Wu
* @Description 全局配置信息表 值对象
* @date 2023-08-12
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "全局配置信息表 值对象")
public class SysConfigVo {
    @Schema(description = "参数名称")
    @JSONField(alternateNames = {"configName"})
    private String configName;
    @Schema(description = "参数键名")
    @JSONField(alternateNames = {"configKey"})
    private String configKey;
    @Schema(description = "参数键值")
    @JSONField(alternateNames = {"configValue"})
    private String configValue;
    @Schema(description = "系统内置（Y是 N否）")
    @JSONField(alternateNames = {"configType"})
    private String configType;
}
