package com.freesia;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Evad.Wu
 * @Description 管理模块 启动类
 * @date 2023-10-27
 */
@EnableAsync
@EnableCaching
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@SpringBootApplication(scanBasePackages = "com.freesia")
@MapperScan(basePackages = {"com.freesia.mapper"})
@EnableJpaRepositories(basePackages = {"com.freesia.repository"})
public class FreesiaAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(FreesiaAdminApplication.class, args);
    }
}
