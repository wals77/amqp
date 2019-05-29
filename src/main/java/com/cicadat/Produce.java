package com.cicadat;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生成者
 */
public class Produce {

    public static final String LDQ_QUEUE = "ldq_queue";
    public static final String LDQ_EXCHANGE = "ldq_exchange";
    public static final String LDQ_ROUTTING = "ldq_routting";

    public static void main(String[] args) throws Exception {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.56.101");
        connectionFactory.setUsername("rabbitstudy");
        connectionFactory.setPassword("123456");
        //创建连接
        Connection connection = connectionFactory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(LDQ_EXCHANGE, "direct");    //申明交换器
        channel.queueDeclare(LDQ_QUEUE, false, false,false, null ); //申明队列
        channel.queueBind(LDQ_QUEUE, LDQ_EXCHANGE, LDQ_ROUTTING);
        channel.basicPublish(LDQ_EXCHANGE, LDQ_ROUTTING, null, "hello world".getBytes());

        channel.close();
        connection.close();

    }
}
