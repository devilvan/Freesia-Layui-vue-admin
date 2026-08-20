package com.freesia.spring.admin.config;

import de.codecentric.boot.admin.server.utils.jackson.AdminServerModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Evad.Wu
 * @Description spring-admin服务端 配置类
 * @date 2025-06-01
 */
@Slf4j
@AutoConfiguration
public class AdminServerConfig {
    @Bean
    public AdminServerModule buildAdminServerModule() {
        String[] arr = new String[]{".*password$", ".*secret$", ".*key$", ".*token$", ".*credentials.*,", ".*vcap_services$"};
        // 注册spring-admin服务端Module
        return new AdminServerModule(arr);
    }
}
