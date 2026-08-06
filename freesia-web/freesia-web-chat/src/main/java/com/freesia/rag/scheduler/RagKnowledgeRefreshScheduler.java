package com.freesia.rag.scheduler;

import com.freesia.rag.service.KnowledgeBaseService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Bliss.Wu
 * @Description 知识库定时刷新 任务
 * @date 2026-08-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RagKnowledgeRefreshScheduler {
    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * 定时刷新知识库（内容未变化则跳过，节省 embedding token）
     * 需在 XXL-Job 调度中心配置任务：JobHandler 填 ragKnowledgeRefreshTask
     *
     * @return 执行结果
     */
    @XxlJob("ragKnowledgeRefreshTask")
    public ReturnT<String> ragKnowledgeRefreshTask() {
        try {
            Map<String, String> result = knowledgeBaseService.loadAllKnowledge();
            log.info("RAG 知识库定时刷新完成: {}", result);
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("RAG 知识库定时刷新失败", e);
            return ReturnT.FAIL;
        }
    }
}
