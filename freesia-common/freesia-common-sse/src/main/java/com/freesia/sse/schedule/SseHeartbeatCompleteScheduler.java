package com.freesia.sse.schedule;

import com.freesia.sse.component.SseEmitterManager;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description SSE接收心跳完成连接 定时任务
 * @date 2025-09-11
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseHeartbeatCompleteScheduler {
    private final SseEmitterManager sseEmitterManager;

    /**
     * 接收心跳-完成连接 定时任务
     */
    @XxlJob("sseHeartbeatScheduler")
    public void sseHeartbeatScheduler() {
        sseEmitterManager.sseHeartbeatSchedule();
    }
}
