package com.example.gamemate.global.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "gamemate.notification.exchange" ;

    // 1. Direct Exchange 선언
    @Bean
    public DirectExchange notificationExchange() {
        // durable: true -> 서버 재시작 시에도 Exchange가 유지됨
        return new DirectExchange(EXCHANGE_NAME);
    }

    // 사용자별 큐는 동적으로 생성되므로, 템플릿만 정의
    // 사용자별 큐 생성은 알림이 처음 필요한 시점(SSE 구독 시)에 처리하는 것이 효율적

    // RabbitTemplate 빈 등록(메시지 발행에 사용)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 메시지 변환기 설정(DTO -> JSON)
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    //JSON 메시지 변환기(DTO를 JSON 형태로 변환하여 전송)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
