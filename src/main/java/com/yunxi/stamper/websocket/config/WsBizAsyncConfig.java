package com.yunxi.stamper.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池的配置
 */
@Configuration
public class WsBizAsyncConfig {

    private static final int MAX_POOL_SIZE = 100;//最大线程数

    private static final int CORE_POOL_SIZE = 20;//核心线程数

    private static final int QUEUE_BLOCK_CAPACITY = 30;//队列最大长度

    private static final int KEEP_ALIVE_SECONDS = 600; //线程池维护线程所允许的空闲时间

    /**
     * 设备端通讯回调(WS)线程池
     *
     * @return
     */
    @Bean("wsBizAsyncTaskExecutor")
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        asyncTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        asyncTaskExecutor.setQueueCapacity(QUEUE_BLOCK_CAPACITY);
        asyncTaskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        asyncTaskExecutor.setThreadNamePrefix("ws-biz-async-task-");
        asyncTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());//拒绝策略，丢弃任务，并抛出异常
        asyncTaskExecutor.initialize();
        return asyncTaskExecutor;
    }
}