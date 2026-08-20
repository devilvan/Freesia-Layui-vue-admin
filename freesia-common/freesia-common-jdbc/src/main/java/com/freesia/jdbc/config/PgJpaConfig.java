package com.freesia.jdbc.config;

import com.alibaba.druid.DbType;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@AutoConfiguration
@AutoConfigureBefore(JpaBaseConfiguration.class)
@EnableJpaRepositories(basePackages = {
}, entityManagerFactoryRef = "pgsqlEntityManagerFactory", transactionManagerRef = "pgsqlTransactionManager")
public class PgJpaConfig {
    @Bean(name = "pgsqlDataSource")
    public DruidDataSource pgsqlDataSource(Environment environment) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setName("pgsql");
        dataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.pgsql.driver-class-name"));
        dataSource.setUrl(environment.getRequiredProperty("spring.datasource.pgsql.jdbc-url"));
        dataSource.setUsername(environment.getRequiredProperty("spring.datasource.pgsql.username"));
        dataSource.setPassword(environment.getRequiredProperty("spring.datasource.pgsql.password"));
        dataSource.setDbType(DbType.postgresql);
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(false);
        dataSource.setKeepAlive(true);
        dataSource.setMinEvictableIdleTimeMillis(300000L);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        dataSource.setUseGlobalDataSourceStat(true);

        Properties connectProperties = new Properties();
        connectProperties.setProperty("druid.stat.mergeSql", "true");
        connectProperties.setProperty("druid.stat.slowSqlMillis", "5000");
        dataSource.setConnectProperties(connectProperties);

        StatFilter statFilter = new StatFilter();
        statFilter.setDbType(DbType.postgresql);
        statFilter.setLogSlowSql(true);
        statFilter.setSlowSqlMillis(2000);
        statFilter.setMergeSql(true);
        dataSource.setProxyFilters(List.of(statFilter));

        return dataSource;
    }

    @Bean(name = "pgsqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean pgsqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("pgsqlDataSource") DruidDataSource dataSource) {
        Map<String, Object> properties = new HashMap<>(16);
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        return builder
                .dataSource(dataSource)
                .packages("com.freesia.po.pgsql",
                        "com.freesia.account.po.pgsql",
                        "com.freesia.icon.po.pgsql",
                        "com.freesia.worldclock.po.pgsql",
                        "com.freesia.deepseek.po.pgsql")
                .persistenceUnit("pgsql")
                .properties(properties)
                .build();
    }

    @Bean(name = "pgsqlTransactionManager")
    public PlatformTransactionManager pgsqlTransactionManager(
            @Qualifier("pgsqlEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }
}
