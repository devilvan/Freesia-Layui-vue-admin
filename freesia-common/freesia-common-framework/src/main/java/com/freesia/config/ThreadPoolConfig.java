package com.freesia.config;

import cn.hutool.core.thread.RejectPolicy;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * @author Evad.Wu
 * @Description 线程池组件
 * @date 2022-07-07
 */
@Configuration
public class ThreadPoolConfig {
    /**
     * 获取CPU核数
     */
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 创建线程池
     *
     * @return 线程池对象
     */
    @Lazy
    @Bean(value = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor buildThreadPoolTaskExecutor() {
        ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(1024);
        ThreadFactory threadFactory = ThreadFactoryBuilder.create().setNamePrefix("freesia-threadPoolTaskExecutor").build();
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(AVAILABLE_PROCESSORS);
        threadPoolTaskExecutor.setMaxPoolSize(AVAILABLE_PROCESSORS * 2);
        threadPoolTaskExecutor.setKeepAliveSeconds(300);
        threadPoolTaskExecutor.setQueueCapacity(1024);
        threadPoolTaskExecutor.setThreadFactory(threadFactory);
        threadPoolTaskExecutor.setRejectedExecutionHandler(RejectPolicy.DISCARD_OLDEST.getValue());
        return threadPoolTaskExecutor;
    }

    /**
     * 创建定时任务线程池
     *
     * @return 线程池对象
     */
    @Lazy
    @Bean(value = "scheduledThreadPoolExecutor")
    public ScheduledThreadPoolExecutor buildScheduledThreadPoolExecutor() {
        ThreadFactory threadFactory = ThreadFactoryBuilder.create().setNamePrefix("freesia-scheduledThreadPoolExecutor").build();
        return new ScheduledThreadPoolExecutor(AVAILABLE_PROCESSORS, threadFactory, RejectPolicy.DISCARD_OLDEST.getValue());
    }
}
