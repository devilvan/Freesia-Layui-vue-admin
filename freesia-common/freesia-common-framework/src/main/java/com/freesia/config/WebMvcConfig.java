package com.freesia.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description Web MVC 配置类
 * @date 2023-08-24
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter;

    /**
     * 项目资源注册器
     *
     * @param registry 注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }

    /**
     * 跨域设置
     *
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("*") // 允许所有域名
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name()) // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有请求头
                .exposedHeaders("Content-Disposition") // 暴露Content-Disposition头
                .allowCredentials(false)
                .maxAge(3600);
    }

    /**
     * Fastjson 处理前端或请求工具导致Long类型数据丢精度问题
     *
     * @param converters Http消息转换器
     * @return 响应报文转换器
     */
    @Bean
    public HttpMessageConverters fastjsonHttpMessageConverter(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter);
        return new HttpMessageConverters(converters);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter);
    }
}
