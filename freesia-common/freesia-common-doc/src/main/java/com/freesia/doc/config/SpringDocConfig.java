package com.freesia.doc.config;


import com.freesia.doc.handler.OpenApiHandler;
import com.freesia.doc.properties.SpringDocProperties;
import com.freesia.util.UString;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description SpringDoc 配置类
 * @date 2023-04-10
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(SpringDocProperties.class)
@AutoConfigureBefore(SpringDocConfiguration.class)
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = true)
public class SpringDocConfig {
    private final ServerProperties serverProperties;

    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI apiInfo(SpringDocProperties springDocProperties) {
        OpenAPI openApi = new OpenAPI();
        // 基本信息
        Optional.of(springDocProperties)
                .map(SpringDocProperties::getInfo)
                .map(infoProperties -> {
                    License license = infoProperties.getLicense();
                    Info info = new Info()
                            .title(infoProperties.getTitle())
                            .description(infoProperties.getDescription())
                            .license(new License().name(license.getName()).url(license.getUrl()))
                            .termsOfService(infoProperties.getTermsOfService())
                            .version(infoProperties.getVersion())
                            .contact(infoProperties.getContact());
                    openApi.info(info);
                    return info;
                });
        // 拓展文档信息
        Optional.of(springDocProperties)
                .map(SpringDocProperties::getExternalDocs)
                .map(externalDocs -> {
                    ExternalDocumentation externalDocumentation = new ExternalDocumentation();
                    externalDocumentation.description(externalDocs.getDescription()).url(externalDocs.getUrl());
                    openApi.externalDocs(externalDocumentation);
                    return externalDocumentation;
                });
        // 组件，用于适配SaToken的校验
        Optional.of(springDocProperties)
                .map(SpringDocProperties::getComponents)
                .map(components -> {
                    openApi.components(components);
                    return components;
                })
                .map(Components::getSecuritySchemes)
                .map(securitySchemes -> {
                    List<SecurityRequirement> list = new ArrayList<>();
                    SecurityRequirement securityRequirement = new SecurityRequirement();
                    Set<String> keySet = securitySchemes.keySet();
                    keySet.forEach(securityRequirement::addList);
                    list.add(securityRequirement);
                    openApi.security(list);
                    return list;
                });
        return openApi;
    }

    /**
     * 自定义 openapi 处理器
     */
    @Bean
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI,
                                         SecurityService securityParser,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers,
                                         Optional<JavadocProvider> javadocProvider) {
        return new OpenApiHandler(openAPI, securityParser, springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomisers, serverBaseUrlCustomisers, javadocProvider);
    }

    /**
     * 对已经生成好的 OpenApi 进行自定义操作
     */
    @Bean
    public OpenApiCustomizer openApiCustomiser() {
        String contextPath = serverProperties.getServlet().getContextPath();
        String finalContextPath;
        if (UString.isBlank(contextPath) || "/".equals(contextPath)) {
            finalContextPath = "";
        } else {
            finalContextPath = contextPath;
        }
        // 对所有路径增加前置上下文路径
        return openApi -> {
            Paths oldPaths = openApi.getPaths();
            if (oldPaths instanceof PlusPaths) {
                return;
            }
            PlusPaths newPaths = new PlusPaths();
            oldPaths.forEach((k, v) -> newPaths.addPathItem(finalContextPath + k, v));
            openApi.setPaths(newPaths);
        };
    }

    /**
     * 单独使用一个类便于判断 解决springdoc路径拼接重复问题
     */
    static class PlusPaths extends Paths {

        @Serial
        private static final long serialVersionUID = -8919859581757417478L;

        public PlusPaths() {
            super();
        }
    }
}
