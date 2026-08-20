package com.freesia.jdbc.config;

import com.alibaba.druid.DbType;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@AutoConfiguration
@AutoConfigureBefore({
        DruidDataSourceAutoConfigure.class,
        DataSourceAutoConfiguration.class,
        MybatisPlusAutoConfiguration.class
})
public class MybatisConfig {
    @Primary
    @Bean(name = "mysqlDataSource")
    public DruidDataSource mysqlDataSource(Environment environment) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setName("mysql");
        dataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.mysql.driver-class-name"));
        dataSource.setUrl(environment.getRequiredProperty("spring.datasource.mysql.jdbc-url"));
        dataSource.setUsername(environment.getRequiredProperty("spring.datasource.mysql.username"));
        dataSource.setPassword(environment.getRequiredProperty("spring.datasource.mysql.password"));
        dataSource.setDbType(DbType.mysql);
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
        connectProperties.setProperty("useCursorFetch", "true");
        connectProperties.setProperty("defaultFetchSize", "1000");
        dataSource.setConnectProperties(connectProperties);

        StatFilter statFilter = new StatFilter();
        statFilter.setDbType(DbType.mysql);
        statFilter.setLogSlowSql(true);
        statFilter.setSlowSqlMillis(2000);
        statFilter.setMergeSql(true);

        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType(DbType.mysql);
        wallFilter.setConfig(wallConfig);

        List<com.alibaba.druid.filter.Filter> filters = new ArrayList<>(2);
        filters.add(statFilter);
        filters.add(wallFilter);
        dataSource.setProxyFilters(filters);
        return dataSource;
    }

    @Primary
    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(
            @Qualifier("mysqlDataSource") DataSource dataSource,
            MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage("com.freesia.*.po");
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:/mapper/*.xml")
        );
        sessionFactory.setPlugins(mybatisPlusInterceptor);
        return sessionFactory.getObject();
    }

    @Bean(name = "mybatisTransactionManager")
    public DataSourceTransactionManager mybatisTransactionManager(
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor());
        return interceptor;
    }

    public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    public PaginationInnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(com.baomidou.mybatisplus.annotation.DbType.MYSQL);
    }

    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }
}
