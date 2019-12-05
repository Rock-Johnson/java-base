package com.github.base.rabbitmq.consumer;


import com.github.base.rabbitmq.config.RabbitConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 简单的消费者，包含simple和work
 */
@Component
@Slf4j
public class RabbitConsumer {


    @RabbitListener(queues = RabbitConstant.QueueSimple)
    public void receive(String message, Message message1, Channel channel) throws IOException {
        log.info("消费者收到消息：{}", message);
        long deliverTag = message1.getMessageProperties().getDeliveryTag();
        //第一个deliveryTag参数为每条信息带有的tag值，第二个multiple参数为布尔类型
        //为true时会将小于等于此次tag的所有消息都确认掉，如果为false则只确认当前tag的信息，可根据实际情况进行选择。
        channel.basicAck(deliverTag, false);
    }
}
