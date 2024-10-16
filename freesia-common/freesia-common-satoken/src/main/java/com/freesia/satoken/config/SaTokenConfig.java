package com.freesia.satoken.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.freesia.component.UrlsComponent;
import com.freesia.satoken.handler.RedisSaTokenHandler;
import com.freesia.satoken.properties.SecurityProperties;
import com.freesia.satoken.service.impl.SaPermissionImpl;
import com.freesia.util.USpring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Evad.Wu
 * @Description Sa-Token 配置类
 * @date 2023-08-25
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {
    private final SecurityProperties securityProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
            UrlsComponent urlsComponent = USpring.getBean("urlsComponent", UrlsComponent.class);
            // 登录验证 -- 排除多个路径
            SaRouter
                    // 获取所有的
                    .match(urlsComponent.getUrls())
                    // 对未排除的路径进行检查，检查是否登录 是否有token
                    .check(StpUtil::checkLogin);
        })).addPathPatterns("/**")
                // 排除不需要拦截的路径
                .excludePathPatterns(securityProperties.getExcludes());
    }

    @Bean
    public StpLogic getStpLogicJwt() {
        // Sa-Token 整合 jwt (简单模式)
        return new StpLogicJwtForSimple();
    }

    /**
     * 权限接口实现(使用bean注入方便用户替换)
     */
    @Bean
    public StpInterface stpInterface() {
        return new SaPermissionImpl();
    }

    /**
     * 自定义dao层存储
     */
    @Bean
    public SaTokenDao saTokenDao() {
        return new RedisSaTokenHandler();
    }

}
