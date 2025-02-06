package com.example.gamemate.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    // 기본 RedisConnectionFactory
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(0);
        return new LettuceConnectionFactory(config);
    }

    // 기본 RedisTemplate
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

    // DB 1: 알림
    @Bean
    public RedisConnectionFactory notificationConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(1);
        return new LettuceConnectionFactory(config);
    }

    // DB 2: 조회수
    @Bean
    public RedisConnectionFactory viewCountConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(2);
        return new LettuceConnectionFactory(config);
    }

    // DB 3: 리프레시 토큰
    @Bean
    public RedisConnectionFactory refreshTokenConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(3);
        return new LettuceConnectionFactory(config);
    }

    // DB 4: 토큰 블랙리스트
    @Bean
    public RedisConnectionFactory blacklistConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(4);
        return new LettuceConnectionFactory(config);
    }

    // 알림 RedisTemplate (DB 1)
    @Bean
    public RedisTemplate<String, Object> notificationRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(notificationConnectionFactory());

        // String 타입을 위한 직렬화 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    // 조회수 RedisTemplate (DB 2)
    @Bean
    public StringRedisTemplate viewCountRedisTemplate() {
        return new StringRedisTemplate(viewCountConnectionFactory());
    }

    // 리프레시 토큰 RedisTemplate (DB 3)
    @Bean
    public StringRedisTemplate refreshTokenRedisTemplate() {
        return new StringRedisTemplate(refreshTokenConnectionFactory());
    }

    // 토큰 블랙리스트 RedisTemplate (DB 4)
    @Bean
    public StringRedisTemplate blacklistRedisTemplate() {
        return new StringRedisTemplate(blacklistConnectionFactory());
    }

    // 쿠폰 RedissonClient (DB 5)
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(5);
        return Redisson.create(config);
    }
}