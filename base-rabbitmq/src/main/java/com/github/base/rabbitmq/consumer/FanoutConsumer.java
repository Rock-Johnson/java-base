package com.github.base.rabbitmq.consumer;

import com.github.base.rabbitmq.config.RabbitConstant;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class FanoutConsumer {

    @RabbitListener(queues = RabbitConstant.QueueFanoutA)
    public void receiveA(String message, Message message1, Channel channel) throws IOException {
        log.info("消费者收到a队列消息：{}", message);
        long deliverTag = message1.getMessageProperties().getDeliveryTag();
        //第一个deliveryTag参数为每条信息带有的tag值，第二个multiple参数为布尔类型
        //为true时会将小于等于此次tag的所有消息都确认掉，如果为false则只确认当前tag的信息，可根据实际情况进行选择。
        channel.basicAck(deliverTag, false);
    }
    @RabbitListener(queues = RabbitConstant.QueueFanoutB)
    public void receiveB(String message, Message message1, Channel channel) throws IOException {
        log.info("消费者收到b队列消息：{}", message);
        long deliverTag = message1.getMessageProperties().getDeliveryTag();
        //第一个deliveryTag参数为每条信息带有的tag值，第二个multiple参数为布尔类型
        //为true时会将小于等于此次tag的所有消息都确认掉，如果为false则只确认当前tag的信息，可根据实际情况进行选择。
        channel.basicAck(deliverTag, false);
    }
}
