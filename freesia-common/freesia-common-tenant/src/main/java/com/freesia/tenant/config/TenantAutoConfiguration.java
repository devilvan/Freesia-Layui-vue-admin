package com.freesia.tenant.config;

import com.freesia.tenant.properties.TenantProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Evad.Wu
 * @Description 多租户 自动配置类
 * @date 2026-08-19
 */
@AutoConfiguration
@EnableConfigurationProperties(TenantProperties.class)
public class TenantAutoConfiguration {
}
