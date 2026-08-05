package com.freesia.jdbc.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description PostgreSQL 配置类
 * @date 2026-08-02
 */
@Configuration
@EnableJpaRepositories(basePackages = {
        "com.freesia.repository.pgsql",
        "com.freesia.account.repository.pgsql",
        "com.freesia.icon.repository.pgsql",
        "com.freesia.worldclock.repository.pgsql",
        "com.freesia.deepseek.repository.pgsql",
}, entityManagerFactoryRef = "pgsqlEntityManagerFactory", transactionManagerRef = "pgsqlTransactionManager")
public class PgJpaConfig {
    /**
     * 创建 PostgreSQL 数据源
     */
    @Bean(name = "pgsqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pgsql")
    public DataSource pgsqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建 JPA EntityManagerFactory
     */
    @Bean(name = "pgsqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean pgsqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("pgsqlDataSource") DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>(16);
        // Hibernate 方言（PostgreSQL）
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        // 禁止 Hibernate 启动时自动执行 DDL（如 alter table），避免擅自修改表结构
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        // 物理命名策略（可选项，将驼峰转为下划线）
        properties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        return builder
                .dataSource(dataSource)
                // PostgreSQL JPA 实体类扫描路径（与 MySQL 实体隔离）
                .packages("com.freesia.po.pgsql",
                        "com.freesia.account.po.pgsql",
                        "com.freesia.icon.po.pgsql",
                        "com.freesia.worldclock.po.pgsql",
                        "com.freesia.deepseek.po.pgsql")
                .persistenceUnit("pgsql")
                .properties(properties)
                .build();
    }

    /**
     * 创建 PostgreSQL 事务管理器
     */
    @Bean(name = "pgsqlTransactionManager")
    public PlatformTransactionManager pgsqlTransactionManager(
            @Qualifier("pgsqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }
}
