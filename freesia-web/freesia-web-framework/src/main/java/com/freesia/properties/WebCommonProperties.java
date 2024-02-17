package com.freesia.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description web模块-基本功能参数 属性类
 * @date 2024-01-12
 */
@Data
@Component
@ConfigurationProperties(prefix = "freesia.web.common")
public class WebCommonProperties {
    @Schema(description = "是否初始化系统配置信息（sysConfig）")
    private Boolean initSysConfig;
    @Schema(description = "是否初始化数据字典（sysDict）")
    private Boolean initSysDict;
}
