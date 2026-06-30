
package com.conduit.transaction_service.rabbitmq;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j

public class TransactionConsumer {

    @Value("${server.port}")
    private String port;

    @RabbitListener(queues = RabbitmqConfig.transactionQueue)
    public void receiveMessage(String payload,
                               Channel channel,
                               Message message) throws IOException {

        long tag = message.getMessageProperties().getDeliveryTag();

        try {

            log.info("Port {} received {}", port, payload);

            // Make only the instance running on 8080 slow
            if ("8081".equals(port)) {
                Thread.sleep(10000);
            }

            channel.basicAck(tag, false);

        } catch (Exception e) {
            channel.basicNack(tag, false, false);
        }
    }
}