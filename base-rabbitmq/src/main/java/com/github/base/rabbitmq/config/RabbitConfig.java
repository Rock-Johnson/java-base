package com.github.base.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {



    @Bean
    public Queue simpleQueue(){
        return new Queue(RabbitConstant.QueueSimple);
    }

    @Bean
    public Queue fanoutAQueue(){
        return new Queue(RabbitConstant.QueueFanoutA);
    }

    @Bean
    public Queue fanoutBQueue(){
        return new Queue(RabbitConstant.QueueFanoutB);
    }


    /**
     * 定义个fanout交换器
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        // 定义一个名为fanoutExchange的fanout交换器
        return new FanoutExchange(RabbitConstant.ExchangeFanout);
    }

    /**
     * 将定义的fanoutA队列与fanoutExchange交换机绑定
     * @return
     */
    @Bean
    public Binding bindingExchangeWithA() {
        return BindingBuilder.bind(fanoutAQueue()).to(fanoutExchange());
    }

    /**
     * 将定义的fanoutB队列与fanoutExchange交换机绑定
     * @return
     */
    @Bean
    public Binding bindingExchangeWithB() {
        return BindingBuilder.bind(fanoutBQueue()).to(fanoutExchange());
    }



    @Bean
    public Queue directAQueue(){
        return new Queue(RabbitConstant.QueueDirectA);
    }

    @Bean
    public Queue directBQueue(){
        return new Queue(RabbitConstant.QueueDirectB);
    }

    @Bean
    public DirectExchange exchangeDirect() {
        // 定义一个名为fanoutExchange的fanout交换器
        return new DirectExchange(RabbitConstant.ExchangeDirect);
    }

    @Bean
    public Binding bindingExchangeWithdirectA() {
        return BindingBuilder.bind(directAQueue()).to(exchangeDirect()).with(RabbitConstant.RoutingKeyA);
    }

    @Bean
    public Binding bindingExchangeWithdirectB() {
        return BindingBuilder.bind(directBQueue()).to(exchangeDirect()).with(RabbitConstant.RoutingKeyB);
    }




    @Bean
    public Queue TopicAQueue(){
        return new Queue(RabbitConstant.QueueTopicA);
    }

    @Bean
    public Queue TopicBQueue(){
        return new Queue(RabbitConstant.QueueTopicB);
    }
    @Bean
    public Queue TopicCQueue(){
        return new Queue(RabbitConstant.QueueTopicC);
    }
    @Bean
    public TopicExchange exchangeTopic() {
        // 定义一个名为fanoutExchange的fanout交换器
        return new TopicExchange(RabbitConstant.ExchangeTopic);
    }

    @Bean
    public Binding bindingExchangeWithTopicA() {
        return BindingBuilder.bind(TopicAQueue()).to(exchangeTopic()).with("topic.msg");
    }

    @Bean
    public Binding bindingExchangeWithTopicB() {
        return BindingBuilder.bind(TopicBQueue()).to(exchangeTopic()).with("topic.#");
    }

    @Bean
    public Binding bindingExchangeWithTopicC() {
        return BindingBuilder.bind(TopicCQueue()).to(exchangeTopic()).with("topic.*.z");
    }
}
