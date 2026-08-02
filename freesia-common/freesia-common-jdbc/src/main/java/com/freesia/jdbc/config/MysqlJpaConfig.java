package com.freesia.jdbc.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description MySQL JPA 配置类（主数据源）
 * @date 2026-08-02
 */
@Configuration
public class MysqlJpaConfig {

    /**
     * 创建 MySQL JPA EntityManagerFactory（默认）
     */
    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>(16);
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");

        return builder
                .dataSource(dataSource)
                .packages("com.freesia.po",
                        "com.freesia.account.po",
                        "com.freesia.icon.po",
                        "com.freesia.worldclock.po",
                        "com.freesia.deepseek.po")
                .persistenceUnit("mysql")
                .properties(properties)
                .build();
    }

    /**
     * 创建 MySQL JPA 事务管理器（默认）
     */
    @Primary
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    /**
     * 创建 TransactionTemplate（基于 MySQL  JPA 事务管理器）
     */
    @Primary
    @Bean(name = "transactionTemplate")
    public TransactionTemplate transactionTemplate(
            @Qualifier("mysqlTransactionManager") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
