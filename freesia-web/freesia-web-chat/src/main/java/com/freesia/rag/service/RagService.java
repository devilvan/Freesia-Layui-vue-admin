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
     * 1. 存储文档：把文档分块、向量化后存入PostgreSQL
     *
     * @param documents 文档
     */
    public void storeDocument(List<Document> documents) {
        pgVectorStore.add(documents);
    }

    /**
     * 2. 检索文档：用户提问时，搜索最相关的Top-K个文档片段
     *
     * @param query 查询条件
     * @return 文档
     */
    public List<Document> searchSimilar(String query) {
        return pgVectorStore.similaritySearch(
                // 返回最相似的5个片段
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );
    }
}
