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

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes={BaseRabbitmqApplication.class})
@Slf4j
public class FanoutProducerTest {

    @Autowired
    private FanoutProducer fanoutProducer;

    @Test
    public void send() {
        fanoutProducer.send(RabbitConstant.ExchangeFanout,"你好,我是send");
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

        fanoutProducer.confirmSend(RabbitConstant.ExchangeFanout,"你好，我是confimSend",confirmCallback ,returnCallback);
    }
}