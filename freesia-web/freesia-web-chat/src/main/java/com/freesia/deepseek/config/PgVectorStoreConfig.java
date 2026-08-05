package com.freesia.deepseek.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Spring AI 的 PgVectorStoreAutoConfiguration 只会注入默认 JdbcTemplate（即 MySQL 主数据源），
 * 无法指定 pgsqlDataSource（Spring AI 1.1.x 的 PgVectorStoreProperties 没有 data-source 属性）。
 * 这里手动声明 PgVectorStore，显式绑定 PostgreSQL 数据源，并让自动配置因 @ConditionalOnMissingBean 退让。
 *
 * @author Evad.Wu
 * @date 2026-08-05
 */
@Configuration
public class PgVectorStoreConfig {

    @Bean
    @ConditionalOnMissingBean(PgVectorStore.class)
    public PgVectorStore pgVectorStore(
            @Qualifier("pgsqlDataSource") DataSource pgsqlDataSource,
            EmbeddingModel embeddingModel,
            PgVectorStoreProperties properties) {
        return PgVectorStore.builder(new JdbcTemplate(pgsqlDataSource), embeddingModel)
                .schemaName(properties.getSchemaName())
                .idType(properties.getIdType())
                .vectorTableName(properties.getTableName())
                .vectorTableValidationsEnabled(properties.isSchemaValidation())
                .dimensions(properties.getDimensions())
                .distanceType(properties.getDistanceType())
                .removeExistingVectorStoreTable(properties.isRemoveExistingVectorStoreTable())
                .indexType(properties.getIndexType())
                .initializeSchema(properties.isInitializeSchema())
                .maxDocumentBatchSize(properties.getMaxDocumentBatchSize())
                .build();
    }
}
