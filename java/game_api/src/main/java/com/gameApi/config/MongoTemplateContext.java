package com.gameApi.config;

import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoTemplateContext {
    private static final ThreadLocal<MongoTemplate> threadLocal = new ThreadLocal<>();

    public static MongoTemplate getMongoTemplate() {
        return threadLocal.get();
    }

    public static void setMongoTemplate(MongoTemplate mongoTemplate) {
        threadLocal.set(mongoTemplate);
    }

    public static void clear() {
        threadLocal.remove();
    }
}

