package com.cicadat;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生成者
 */
public class Produce {

    public static final String LDQ_QUEUE = "ldq_queue";
    public static final String LDQ_QUEUE_TWO = "ldq_queue_two";
    public static final String LDQ_EXCHANGE = "ldq_exchange";
    public static final String LDQ_ROUTTING = "ldq_routting";
    public static final String LDQ_ROUTTING_TWO = "ldq_routting_two";

    public static void main(String[] args) throws Exception {
        Channel channel = ConnectionUtils.createChannel();
        channel.exchangeDeclare(LDQ_EXCHANGE, "direct");    //申明交换器
        channel.queueDeclare(LDQ_QUEUE, false, false,false, null ); //申明队列

        channel.queueDeclare(LDQ_QUEUE_TWO, true, false,false, null );

        /*channel.queueBind(LDQ_QUEUE, LDQ_EXCHANGE, "log.#");

        channel.queueBind(LDQ_QUEUE_TWO, LDQ_EXCHANGE, "log.*");*/

        channel.queueBind(LDQ_QUEUE, LDQ_EXCHANGE, LDQ_ROUTTING);

        channel.queueBind(LDQ_QUEUE_TWO, LDQ_EXCHANGE, LDQ_ROUTTING);

        channel.basicPublish(LDQ_EXCHANGE, LDQ_ROUTTING, null, "hello info".getBytes());

       // channel.basicPublish(LDQ_EXCHANGE, "log.error", null, "hello error".getBytes());

        ConnectionUtils.release();

    }
}
