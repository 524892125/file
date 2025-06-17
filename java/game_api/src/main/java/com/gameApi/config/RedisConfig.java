package com.gameApi.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean(name = "primaryRedisTemplate")
    @Primary
    public RedisTemplate<Object, Object> primaryRedisTemplate(
            @Qualifier("primaryRedisConnectionFactory") RedisConnectionFactory factory) {
        return createRedisTemplate(factory);
    }
    @Primary
    @Bean(name = "primaryStringRedisTemplate")
    public StringRedisTemplate primaryStringRedisTemplate(
            @Qualifier("primaryRedisConnectionFactory") RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
    @Primary
    @Bean
    public RedisConnectionFactory primaryRedisConnectionFactory(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port,
            @Value("${spring.redis.password}") String password,
            @Value("${spring.redis.database}") int database) {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(RedisPassword.of(password));
        config.setDatabase(database);
        return new LettuceConnectionFactory(config);
    }

    private RedisTemplate<Object, Object> createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 使用默认的序列化配置或自定义
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
