package com.freesia.rag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Bliss.Wu
 * @Description RAG集成 业务逻辑
 * @date 2026-08-06
 */
@Service
@RequiredArgsConstructor
public class RagService {
    private final PgVectorStore pgVectorStore;

    /**
     * 存储文档：把文档分块、向量化后存入PostgreSQL
     *
     * @param documents 文档
     */
    public void storeDocument(List<Document> documents) {
        pgVectorStore.add(documents);
    }

    /**
     * 检索文档：用户提问时，搜索最相关的 Top-K 个文档片段（默认5个）
     *
     * @param query 查询条件
     * @return 文档
     */
    public List<Document> searchSimilar(String query) {
        return searchSimilar(query, 5, null);
    }

    /**
     * 检索文档：指定返回片段数量
     *
     * @param query 查询条件
     * @param topK  返回片段数量
     * @return 文档
     */
    public List<Document> searchSimilar(String query, int topK) {
        return searchSimilar(query, topK, null);
    }

    /**
     * 检索文档：指定返回片段数量 + 按来源过滤（如 README / swagger）
     *
     * @param query  查询条件
     * @param topK   返回片段数量
     * @param source 来源，null 表示不过滤
     * @return 文档
     */
    public List<Document> searchSimilar(String query, int topK, String source) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(query)
                .topK(topK);
        if (source != null && !source.isEmpty()) {
            builder.filterExpression("source == '" + source + "'");
        }
        return pgVectorStore.similaritySearch(builder.build());
    }

    /**
     * 删除指定来源的全部向量数据（幂等投喂时先删旧数据再写入）
     *
     * @param source 来源（metadata.source）
     */
    public void deleteBySource(String source) {
        if (source == null || source.isEmpty()) {
            return;
        }
        pgVectorStore.delete("source == '" + source + "'");
    }
}
