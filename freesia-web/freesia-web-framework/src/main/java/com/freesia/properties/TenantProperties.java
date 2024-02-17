package com.freesia.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description 租户相关配置 属性类
 * @date 2024-01-26
 */
@Data
@Component
@ConfigurationProperties(prefix = "freesia.web.tenant")
public class TenantProperties {
    @Schema(description = "是否开启（默认false）")
    private Boolean enabled = false;
    @Schema(description = "租户数据库字段")
    private String tenantColumn;
    @Schema(description = "排除租户过滤的数据表（逗号分隔）")
    private String ignoreTable;
}
