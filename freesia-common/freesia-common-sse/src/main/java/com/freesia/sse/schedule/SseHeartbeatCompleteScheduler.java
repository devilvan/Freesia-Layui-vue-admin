package com.freesia.sse.schedule;

import com.freesia.sse.component.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
    @Scheduled(fixedRate = 20000)
    public void sendHeartbeat() {
        sseEmitterManager.sseHeartbeatSchedule();
    }
}
