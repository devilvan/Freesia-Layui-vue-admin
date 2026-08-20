package com.freesia.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.freesia.interceptor.PlusDataPermissionInterceptor;
import com.freesia.jdbc.config.MybatisConfig;
import com.freesia.tenant.handler.TenantHandler;
import com.freesia.tenant.properties.TenantProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;

import jakarta.annotation.PostConstruct;

/**
 * @author Evad.Wu
 * @Description Web模块Mybatis 配置类
 * @date 2024-01-29
 */
@AutoConfiguration
@RequiredArgsConstructor
@AutoConfigureBefore(MybatisConfig.class)
public class WebMybatisConfig {
    private final TenantProperties tenantProperties;
    private final MybatisPlusInterceptor mybatisPlusInterceptor;

    @PostConstruct
    public void init() {
        mybatisPlusInterceptor.addInnerInterceptor(plusDataPermissionInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(tenantLineInnerInterceptor());
    }

    public PlusDataPermissionInterceptor plusDataPermissionInterceptor() {
        return new PlusDataPermissionInterceptor();
    }

    public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
        return new TenantLineInnerInterceptor(new TenantHandler(tenantProperties));
    }
}

