package com.cicadat;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 消费者
 */
public class Consumer {

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
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(LDQ_EXCHANGE, "direct");    //申明交换器
        channel.queueDeclare(LDQ_QUEUE, false, false,false, null ); //申明队列
        channel.queueBind(LDQ_QUEUE, LDQ_EXCHANGE, LDQ_ROUTTING);
        String message = channel.basicConsume(LDQ_QUEUE, new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
        TimeUnit.SECONDS.sleep(1);
        channel.close();
        connection.close();

    }
}
