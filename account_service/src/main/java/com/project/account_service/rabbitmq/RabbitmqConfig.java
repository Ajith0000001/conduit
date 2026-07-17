
package com.project.account_service.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class RabbitmqConfig {

    public static final String accountExchange = "account-events";
    public static final String notificationQueue = "notification-queue";
    public static final String transactionQueue = "transaction-queue";

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback((correlationData,ack,cause) ->{
            if(ack) {
            log.info("Message delivered with correation id of {}",correlationData.getId());           
            // handle the outbox success status setting witn correlation id
            } else {
                log.info("Delivery Failed {}",cause);
            }
        });
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;

    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange accouTopicExchange() {
        return new TopicExchange(accountExchange);
    }

    @Bean
    public DirectExchange retryExchange() {
        System.out.println("creating retry exchange quque...");
        return new DirectExchange("retry-exchange");
    }

    @Bean
    public DirectExchange finalDeadExchange() {
        return new DirectExchange("final-dlx");
    }

    @Bean
    public Queue notificatQueueFn() {
        return new Queue(notificationQueue, true);
    }

    @Bean
    public Queue failedQueue() {
        System.out.println("creating failed quque...");
        return new Queue("failed-queue");
    }

    @Bean
    public Queue transactQueueFn() {
        System.out.println("creating transaction quque...");
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "retry-exchange");
        args.put("x-dead-letter-routing-key", "retry");
        return new Queue(transactionQueue, true, false, false, args);
    }

    @Bean
    public Queue retryQueue() {
        System.out.println("creating retry quque...");

        Map<String, Object> args = new HashMap<>();

        args.put("x-message-ttl", 5000);

        args.put("x-dead-letter-exchange", "account-events");

        args.put("x-dead-letter-routing-key", "account.created");

        return new Queue("retry-queue", true, false, false, args);
    }

    @Bean
    public Binding accountTopicExchange_notificationQueue_binding() {
        return BindingBuilder.bind(notificatQueueFn())
                .to(accouTopicExchange())
                .with("account.*");
    }

    @Bean
    public Binding retryBinding() {

        return BindingBuilder
                .bind(retryQueue())
                .to(retryExchange())
                .with("retry");
    }

    @Bean
    public Binding failedBinding() {

        return BindingBuilder
                .bind(failedQueue())
                .to(finalDeadExchange())
                .with("failed");
    }

    @Bean
    public Binding accountTopicExchange_transaction_binding() {
        return BindingBuilder.bind(transactQueueFn())
                .to(accouTopicExchange())
                .with("account.created");
    }

}
