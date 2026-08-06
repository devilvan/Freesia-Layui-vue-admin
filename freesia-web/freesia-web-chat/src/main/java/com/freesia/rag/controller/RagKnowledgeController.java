package com.freesia.rag.controller;

import com.freesia.rag.service.KnowledgeBaseService;
import com.freesia.util.UCollection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Bliss.Wu
 * @Description 知识库管理 控制类
 * @date 2026-08-06
 */
@Slf4j
@RestController
@RequestMapping("/api/rag/knowledge")
@RequiredArgsConstructor
@Tag(name = "RagKnowledgeController", description = "知识库管理 控制器")
public class RagKnowledgeController {
    private final KnowledgeBaseService knowledgeBaseService;

    @Operation(summary = "刷新知识库（重新投喂 README 与 Swagger，内容未变化则跳过）")
    @PostMapping("/refresh")
    public Map<String, Object> refresh() {
        Map<String, Object> result = UCollection.optimizeInitialCapacityMap();
        try {
            Map<String, String> sources = knowledgeBaseService.loadAllKnowledge();
            result.put("success", true);
            result.put("sources", sources);
        } catch (Exception e) {
            log.error("刷新知识库失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
