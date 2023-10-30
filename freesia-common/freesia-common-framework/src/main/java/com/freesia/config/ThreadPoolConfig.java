package com.freesia.config;

import cn.hutool.core.thread.RejectPolicy;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    @Bean(value = "threadPoolExecutor")
    public ThreadPoolExecutor getThreadPoolExecutor() {
        ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(1024);
        ThreadFactory threadFactory = ThreadFactoryBuilder.create().setNamePrefix("betrice-threadPoolExecutor").build();
        return new ThreadPoolExecutor(
                AVAILABLE_PROCESSORS, AVAILABLE_PROCESSORS * 4, 300,
                TimeUnit.SECONDS, blockingQueue, threadFactory, RejectPolicy.DISCARD_OLDEST.getValue()
        );
    }
}
