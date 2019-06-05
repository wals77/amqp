package com.cicadat.example;

import com.cicadat.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 订单超时自动取消
 */
public class OrderConsumer {

    public void orderCancel() throws Exception {
        Connection connection = ConnectionUtils.createConnection();
        final Channel channel = connection.createChannel();
        channel.basicConsume("order.dead.queue", false, new DefaultConsumer(channel){

            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                String orderId = new String(body);
                Order order = new Order();
                order.setId(orderId);
                order.setStatus(1);
                System.out.println(orderId+"订单取消成功了");
                channel.basicAck(deliveryTag, false);
            }
        });

        /*channel.close();
        connection.close();*/
    }
}
