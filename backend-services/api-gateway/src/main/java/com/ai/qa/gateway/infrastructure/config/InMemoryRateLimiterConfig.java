package com.ai.qa.gateway.infrastructure.config;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class InMemoryRateLimiterConfig {

    private static final Logger log = LoggerFactory.getLogger(InMemoryRateLimiterConfig.class);
    // ipKeyResolver Bean 保持不变
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    /**
     * 自定义的内存限流器 Bean (完整版)
     */
    @Bean
    @Primary
    public org.springframework.cloud.gateway.filter.ratelimit.RateLimiter<InMemoryRateLimiterConfig.RateLimiterConfig> inMemoryRateLimiter() {

        // 定义默认的限流速率
        final double defaultReplenishRate = 500.0; // 每秒生成的令牌数
        final int defaultBurstCapacity = 100000;     // 令牌桶总容量

        return new org.springframework.cloud.gateway.filter.ratelimit.RateLimiter<RateLimiterConfig>() {

            private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();
            private final ConcurrentHashMap<String, RateLimiterConfig> configs = new ConcurrentHashMap<>();

            @Override
            public Mono<Response> isAllowed(String routeId, String id) {
                // routeId 是当前请求匹配的路由ID
                // id 是 KeyResolver 解析出的 key (IP地址)
                // 获取当前路由的配置，如果不存在则使用默认配置
                RateLimiterConfig config = configs.getOrDefault(routeId, new RateLimiterConfig(defaultReplenishRate, defaultBurstCapacity));

                // 根据 IP 地址获取或创建 Guava RateLimiter
                RateLimiter limiter = limiters.computeIfAbsent(id, k -> {
                    // 使用配置的速率来创建限流器
                    RateLimiter newLimiter = RateLimiter.create(config.getReplenishRate());
                    // 预热 Guava RateLimiter (可选，但更好)，让令牌桶初始时就是满的
                    newLimiter.tryAcquire(config.getBurstCapacity());
                    return newLimiter;
                });

                // 尝试获取一个令牌
                boolean allowed = limiter.tryAcquire();

                if (allowed) {
                    log.info("Request ALLOWED. Route: {}, Key: {}", routeId, id);
                    return Mono.just(new Response(true, new HashMap<>()));
                } else {
                    log.warn("Request DENIED (Rate Limited). Route: {}, Key: {}", routeId, id);
                    // 当被限流时，可以返回一些有用的头信息
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-RateLimit-Remaining", "0");
                    headers.put("X-RateLimit-Burst-Capacity", String.valueOf(config.getBurstCapacity()));
                    headers.put("X-RateLimit-Replenish-Rate", String.valueOf(config.getReplenishRate()));
                    return Mono.just(new Response(false, headers));
                }
            }

            // =======================================================
            // 补全缺失的方法
            // =======================================================
            @Override
            public Map<String, RateLimiterConfig> getConfig() {
                // 返回当前所有路由的配置信息
                return this.configs;
            }
            // =======================================================

            @Override
            public Class<RateLimiterConfig> getConfigClass() {
                return RateLimiterConfig.class;
            }

            @Override
            public RateLimiterConfig newConfig() {
                // 提供一个默认的空配置对象
                return new RateLimiterConfig();
            }
        };
    }

    /**
     * 配置类，用于存储限流参数
     * 现在它不再是空的了，包含了速率和容量
     */
    public static class RateLimiterConfig {
        private double replenishRate;
        private int burstCapacity;

        public RateLimiterConfig() {
        }

        public RateLimiterConfig(double replenishRate, int burstCapacity) {
            this.replenishRate = replenishRate;
            this.burstCapacity = burstCapacity;
        }

        // Getters and Setters
        public double getReplenishRate() {
            return replenishRate;
        }
        public void setReplenishRate(double replenishRate) {
            this.replenishRate = replenishRate;
        }
        public int getBurstCapacity() {
            return burstCapacity;
        }
        public void setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
        }
    }
}