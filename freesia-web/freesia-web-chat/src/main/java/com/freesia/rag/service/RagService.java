package com.freesia.rag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagService {

    private final VectorStore vectorStore;


    // 1. 存储文档：把文档分块、向量化后存入PostgreSQL
    public void storeDocument(List<Document> documents) {
        vectorStore.add(documents);
    }

    // 2. 检索文档：用户提问时，搜索最相关的Top-K个文档片段
    public List<Document> searchSimilar(String query) {
        return vectorStore.similaritySearch(
                // 返回最相似的5个片段
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );
    }
}
