package com.example.gamemate.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // LettuceConnectionFactory를 DB별로 하나씩 생성하여 Bean으로 관리
    // DB 0: 알림
    @Bean
    @Primary
    public LettuceConnectionFactory notificationConnectionFactory() {
        return createLettuceConnectionFactory(0);
    }

    // DB 1: 조회수
    @Bean
    public LettuceConnectionFactory viewCountConnectionFactory() {
        return createLettuceConnectionFactory(1);
    }

    // DB 2: 리프레시 토큰
    @Bean
    public LettuceConnectionFactory refreshTokenConnectionFactory() {
        return createLettuceConnectionFactory(2);
    }

    // DB 3: 토큰 블랙리스트
    @Bean
    public LettuceConnectionFactory tokenBlacklistConnectionFactory() {
        return createLettuceConnectionFactory(3);
    }

    // DB 4: 쿠폰
    @Bean
    public LettuceConnectionFactory couponConnectionFactory() {
        return createLettuceConnectionFactory(4);
    }

    // 공통: LettuceConnectionFactory 생성
    private LettuceConnectionFactory createLettuceConnectionFactory(int database) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        configuration.setDatabase(database);
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    // 알림 RedisTemplate (DB 0)
    @Bean
    @Primary
    public RedisTemplate<String, Object> notificationRedisTemplate(
            @Qualifier("notificationConnectionFactory") LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

    // 조회수 RedisTemplate (DB 1)
    @Bean
    public StringRedisTemplate viewCountRedisTemplate(
            @Qualifier("viewCountConnectionFactory") LettuceConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    // 리프레시 토큰 RedisTemplate (DB 2)
    @Bean
    public StringRedisTemplate refreshTokenRedisTemplate(
            @Qualifier("refreshTokenConnectionFactory") LettuceConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    // 토큰 블랙리스트 RedisTemplate (DB 3)
    @Bean
    public StringRedisTemplate tokenBlacklistRedisTemplate(
            @Qualifier("tokenBlacklistConnectionFactory") LettuceConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    // 쿠폰 RedisTemplate (DB 4)
    @Bean
    public StringRedisTemplate couponRedisTemplate(
            @Qualifier("couponConnectionFactory") LettuceConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}