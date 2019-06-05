package com.cicadat.example;

import com.cicadat.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class OrderProducer {

    /**
     * 创建订单
     */
    public Order createOrder() throws Exception {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setStatus(0);
        order.setCreateTime(new Date());
        Connection connection = ConnectionUtils.createConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("order.exchange",BuiltinExchangeType.DIRECT);

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange","order.dead.exchange"); //死信队列
        channel.queueDeclare("order.queue",true,false,false, arguments);

        channel.exchangeDeclare("order.dead.exchange", BuiltinExchangeType.DIRECT);
        channel.queueDeclare("order.dead.queue",true,false,false, null);    //死信队列
        channel.queueBind("order.dead.queue","order.dead.exchange","order.routingkey");
        channel.queueBind("order.queue","order.exchange","order.routingkey");
        try {
            channel.txSelect();
            channel.basicPublish("order.exchange","order.routingkey",new AMQP.BasicProperties().builder().deliveryMode(2).expiration("10000").build(), order.getId().getBytes());    //3秒超时
            System.out.println("订单消息发送成功，id= "+ order.getId());
            channel.txCommit();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("订单消息发送失败");
            channel.txRollback();
        } finally {
            channel.close();
            connection.close();
        }


        return order;
    }

}
