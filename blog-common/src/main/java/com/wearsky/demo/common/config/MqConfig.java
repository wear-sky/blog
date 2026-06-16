package com.wearsky.demo.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqConfig {

    private final RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setReturnsCallback(returnedMessage ->
                log.error("""
                                触发return callback：
                                exchange: {}
                                routingKey: {}
                                message: {}
                                replyCode: {}
                                replyText: {}"""
                        , returnedMessage.getExchange()
                        , returnedMessage.getRoutingKey()
                        , returnedMessage.getMessage()
                        , returnedMessage.getReplyCode()
                        , returnedMessage.getReplyText()));
    }

    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, "error", "error");
    }
}
