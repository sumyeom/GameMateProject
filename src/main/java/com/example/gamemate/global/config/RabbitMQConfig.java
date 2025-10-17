package com.example.gamemate.global.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue
    public static final String EMAIL_QUEUE = "email.verification.queue";
    public static final String EMAIL_DLQ = "email.verification.dlq";

    // Exchange
    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String EMAIL_DLX = "email.dlx";

    // Routing Key
    public static final String EMAIL_ROUTING_KEY = "email.verification";

    /**
     * 이메일 발송 Queue
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .deadLetterExchange(EMAIL_DLX)
                .deadLetterRoutingKey("email.failed")
                .ttl(600000)
                .build();
    }

    /**
     * Dead Letter Queue(실패한 이메일)
     */
    @Bean
    public Queue emailDeadLetterQueue() {
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }

    /**
     * Exchange
     */
    @Bean
    public TopicExchange emailExchange(){
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public TopicExchange emailDeadLetterExchange(){
        return new TopicExchange(EMAIL_DLX);
    }

    /**
     * Binding
     */
    @Bean
    public Binding emailBinding(){
        return BindingBuilder
                .bind(emailQueue())
                .to(emailExchange())
                .with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding emailDlxBinding(){
        return BindingBuilder
                .bind(emailDeadLetterQueue())
                .to(emailDeadLetterExchange())
                .with("email.failed");
    }

    /**
     * JSON 변환기
     */
    @Bean
    public Jackson2JsonMessageConverter emailMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate(이메일 전용)
     */
    @Bean
    public RabbitTemplate emailRabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(emailMessageConverter());
        return template;
    }

    /**
     * Consumer Factory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory emailListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(emailMessageConverter());

        // Consumer 설정
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        factory.setPrefetchCount(10);
        return factory;
    }
}
