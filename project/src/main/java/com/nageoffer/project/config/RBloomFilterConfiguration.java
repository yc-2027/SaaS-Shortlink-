package com.nageoffer.project.config;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 布隆过滤器配置
 */
@Slf4j
@Configuration(value = "rBloomFilterConfigurationByAdmin")
@RequiredArgsConstructor
public class RBloomFilterConfiguration {
    RBloomFilter<String> cachePenetrationBloomFilter;
    private final StringRedisTemplate redis;

    /**
     * 防止短链接创建查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortUriCreateCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100_000_000L, 0.001);
        return cachePenetrationBloomFilter;
    }
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkBloomSaturation() {
        long bits = cachePenetrationBloomFilter.getSize();
        long set  = redis.execute((RedisCallback<Long>) c -> c.bitCount(cachePenetrationBloomFilter.getName().getBytes()));
        if (set * 1.0 / bits > 0.8) {
            // 创建新 Bloom、切换引用 → 方案详见 Stable/Scalable BF  :contentReference[oaicite:10]{index=10}
            log.info("内存空间不足，该创建新bloom filter了");
        }
    }
}