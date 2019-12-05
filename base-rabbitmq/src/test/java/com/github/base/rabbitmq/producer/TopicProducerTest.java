package com.github.base.rabbitmq.producer;

import com.github.base.rabbitmq.BaseRabbitmqApplication;
import com.github.base.rabbitmq.config.RabbitConstant;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={BaseRabbitmqApplication.class})
@Slf4j
public class TopicProducerTest {

    @Autowired
    private TopicProducer topicProducer;

    @Test
    public void send() {
        topicProducer.send(RabbitConstant.ExchangeTopic, "topic.msg","你好,我是topic.msg");
        topicProducer.send(RabbitConstant.ExchangeTopic, "topic.good.msg","你好,我是topic.good.msg");
        topicProducer.send(RabbitConstant.ExchangeTopic, "topic.m.z","你好,我是topic.m.z");
    }

    @Test
    public void testSend() {
        /**
         * 如果消息没有到exchange,则confirm回调,ack=false
         * 如果消息到达exchange,则confirm回调,ack=true
         * exchange到queue成功,则不回调return
         * exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
         */
        RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送失败：correlationData: {},cause: {}", correlationData, cause);
            }else {
                log.info("消息发送成功：correlationData: {},ack: {}", correlationData, ack);
            }
        };

        RabbitTemplate.ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routeKey) ->
                log.error("消息丢失: exchange: {},routeKey: {},replyCode: {},replyText: {}", exchange, routeKey, replyCode, replyText);
        topicProducer.confirmSend(RabbitConstant.ExchangeTopic, "topic.msg","你好,我是topic.msg",confirmCallback ,returnCallback);
        topicProducer.confirmSend(RabbitConstant.ExchangeTopic, "topic.good.msg","你好,我是topic.good.msg",confirmCallback ,returnCallback);
        topicProducer.confirmSend(RabbitConstant.ExchangeTopic, "topic.m.z","你好,我是topic.m.z",confirmCallback ,returnCallback);

    }
}