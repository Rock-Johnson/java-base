package com.github.base.rabbitmq.config;

public class RabbitConstant {

    // 普通
    public final static String QueueSimple = "simple";

    //广播
    public final static String QueueFanoutA = "fanout.a";

    public final static String QueueFanoutB = "fanout.b";

    public final static String ExchangeFanout = "fanout";

    //路由
    public final static String QueueDirectA = "direct.a";
    public final static String QueueDirectB = "direct.b";
    public final static String ExchangeDirect = "direct";
    public final static String RoutingKeyA = "A";
    public final static String RoutingKeyB = "B";


    //topic
    public final static String QueueTopicA = "topic.a";
    public final static String QueueTopicB = "topic.b";
    public final static String QueueTopicC = "topic.c";
    public final static String ExchangeTopic = "topic";
}
