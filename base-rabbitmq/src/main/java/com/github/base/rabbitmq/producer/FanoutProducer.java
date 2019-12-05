package com.github.base.rabbitmq.producer;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 *  fanout模式
 * fanout属于广播模式，只要跟它绑定的队列都会通知并且接受到消息。
 */
@Component
public class FanoutProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String exchange,String message){
        rabbitTemplate.convertAndSend(exchange,"",message);
    }

    /**
     * 有确认的发送
     * @param exchange
     * @param msg
     * @param confirmCallback
     * @param returnCallback
     */
    public void confirmSend(String exchange, String msg, RabbitTemplate.ConfirmCallback confirmCallback,RabbitTemplate.ReturnCallback returnCallback){
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
        rabbitTemplate.convertAndSend(exchange,"",message,correlationData);
    }


}
