package com.gameApi.config;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisTemplateContext {
    private static final ThreadLocal<RedisTemplate> threadLocal = new ThreadLocal<>();

    public static RedisTemplate getMongoTemplate() {
        return threadLocal.get();
    }

    public static void setMongoTemplate(RedisTemplate redisTemplate) {
        threadLocal.set(redisTemplate);
    }

    public static void clear() {
        threadLocal.remove();
    }
}
