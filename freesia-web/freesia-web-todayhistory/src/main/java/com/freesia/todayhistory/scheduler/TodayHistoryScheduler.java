package com.freesia.todayhistory.scheduler;

import com.freesia.todayhistory.service.TodayHistoryService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 历史上的今天 XXL-Job 定时任务。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TodayHistoryScheduler {
    private final TodayHistoryService todayHistoryService;

    @XxlJob("todayHistorySyncTask")
    public ReturnT<String> todayHistorySyncTask() {
        try {
            List<?> result = todayHistoryService.syncAll(true);
            log.info("历史上的今天同步完成，成功同步 {} 天", result.size());
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("历史上的今天同步失败", e);
            return ReturnT.FAIL;
        }
    }
}
