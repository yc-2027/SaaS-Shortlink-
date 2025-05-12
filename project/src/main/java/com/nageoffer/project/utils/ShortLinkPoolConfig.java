package com.nageoffer.project.utils;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.BlockingQueue;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ShortLinkPoolConfig {

    @Bean("shortlinkExecutor")
    public ThreadPoolTaskExecutor shortlinkExecutor(MeterRegistry meter) {
        int cpu = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(cpu);
        exec.setMaxPoolSize(cpu * 2);
        exec.setQueueCapacity(5000);
        exec.setKeepAliveSeconds(60);
        exec.setThreadNamePrefix("sl-exec-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();                                    // 别忘了初始化

        // 监控队列长度
        Gauge.builder("sl.pool.queue",
                        exec.getThreadPoolExecutor().getQueue(),
                        BlockingQueue::size)
                .description("shortlink queue size")
                .register(meter);

        // 监控活跃线程
        Gauge.builder("sl.pool.active",
                        exec.getThreadPoolExecutor(),
                        ThreadPoolExecutor::getActiveCount)
                .description("shortlink active threads")
                .register(meter);

        return exec;
    }
}
