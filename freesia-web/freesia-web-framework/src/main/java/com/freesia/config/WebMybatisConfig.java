package com.freesia.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.freesia.interceptor.PlusDataPermissionInterceptor;
import com.freesia.tenant.handler.TenantHandler;
import com.freesia.tenant.properties.TenantProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;

/**
 * @author Evad.Wu
 * @Description Web模块Mybatis 配置类
 * @date 2024-01-29
 */
@Configuration
@RequiredArgsConstructor
@DependsOn(value = "mybatisConfig")
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

