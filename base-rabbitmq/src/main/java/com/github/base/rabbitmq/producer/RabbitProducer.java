package com.github.base.rabbitmq.producer;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * simple或者是work的生产者
 */
@Component
public class RabbitProducer {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String queue,String message){
        rabbitTemplate.convertAndSend(queue,message);
    }

    /**
     * 有确认的发送
     * @param queue
     * @param msg
     * @param confirmCallback
     * @param returnCallback
     */
    public void confirmSend(String queue, String msg, RabbitTemplate.ConfirmCallback confirmCallback,RabbitTemplate.ReturnCallback returnCallback){
        // 构建回调返回的数据
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(System.currentTimeMillis() + "");

        Message message = MessageBuilder.withBody(msg.toString().getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                // 将CorrelationData的id 与 Message的correlationId绑定，然后关系保存起来,然后人工处理
                .setCorrelationId(correlationData.getId())
                .build();

        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.convertAndSend(queue,message,correlationData);
    }

}
