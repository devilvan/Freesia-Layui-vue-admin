package com.freesia.jdbc.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;

import javax.sql.DataSource;

/**
 * @author Evad.Wu
 * @Description Mybatis配置类
 * PaginationInnerInterceptor 分页插件，自动识别数据库类型
 * https://baomidou.com/pages/97710a/
 * OptimisticLockerInnerInterceptor 乐观锁插件
 * https://baomidou.com/pages/0d93c0/
 * MetaObjectHandler 元对象字段填充控制器
 * https://baomidou.com/pages/4c6bcf/
 * ISqlInjector sql注入器
 * https://baomidou.com/pages/42ea4a/
 * BlockAttackInnerInterceptor 如果是对全表的删除或更新操作，就会终止该操作
 * https://baomidou.com/pages/f9a237/
 * IllegalSQLInnerInterceptor sql性能规范插件(垃圾SQL拦截)
 * IdentifierGenerator 自定义主键策略
 * https://baomidou.com/pages/568eb2/
 * TenantLineInnerInterceptor 多租户插件
 * https://baomidou.com/pages/aef2f2/
 * DynamicTableNameInnerInterceptor 动态表名插件
 * https://baomidou.com/pages/2a45ff/
 * @date 2022-07-13
 */
@AutoConfiguration
@AutoConfigureBefore({
        DruidDataSourceAutoConfigure.class,
        DataSourceAutoConfiguration.class,
        MybatisPlusAutoConfiguration.class
})
public class MybatisConfig {
    /**
     * 创建 MySQL 数据源
     */
    @Primary
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建 MyBatis-Plus 的 SqlSessionFactory
     */
    @Primary
    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(
            @Qualifier("mysqlDataSource") DataSource dataSource,
            MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage("com.freesia.*.po");
        // Mapper XML 文件位置（如果有）
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:/mapper/*.xml")
        );
        // 添加 MyBatis-Plus 插件（如分页插件）
        sessionFactory.setPlugins(mybatisPlusInterceptor);
        return sessionFactory.getObject();
    }

    /**
     * 创建 MyBatis 事务管理器（供 MyBatis-Plus 使用）
     */
    @Bean(name = "mybatisTransactionManager")
    public DataSourceTransactionManager mybatisTransactionManager(
            @Qualifier("mysqlDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * Mybatis-Plus拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor());
        return interceptor;
    }

    /**
     * 全局修改拦截器
     */
    public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    /**
     * 分页插件拦截器
     * DbType：数据库类型(根据类型获取应使用的分页方言)
     */
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor(DbType.MYSQL);
    }

    /**
     * 乐观锁插件拦截器
     */
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }
}
