
package com.project.account_service.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    public static final String transactionQueue = "transaction-queue";
    public static final String directExchange = "transaction-exchange";

    @Bean 
    public Queue transactionQueue() {
        return new Queue(transactionQueue,true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(directExchange);
    }

    @Bean
    public Binding binding() {
        return  BindingBuilder
                    .bind(transactionQueue())
                    .to(directExchange())
                    .with("transaction");
    }
    
}
