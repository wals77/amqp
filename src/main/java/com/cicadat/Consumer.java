package com.cicadat;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 消费者
 */
public class Consumer {


    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtils.createConnection();
        final Channel channel = connection.createChannel();

//        basicGetTest(channel);


        consumeTest(channel);

//        rejectTest(channel);
        /*TimeUnit.SECONDS.sleep(500000);
        channel.close();
        connection.close();*/

    }


    public static void consumeTest(final Channel channel) throws Exception{


        //服务端会把所有消息都推送给消费者
        channel.basicQos(10);   //qos(未确认的消息的数量)是针对消费者推送模式的，当队列的消息过多，防止队列一下把所有消息都推给消费者，可以设置这个值
        channel.basicConsume("myQueue", false, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(consumerTag +"我接收到的消息："+new String(body));

                long deliveryTag = envelope.getDeliveryTag();
                channel.basicAck(deliveryTag, false);   //ack确认，它会循环发送接收到消息的ack
            }
        });
    }

    //客户端主动从服务器拿消息
    public static void basicGetTest(Channel channel) throws Exception{
        GetResponse response = channel.basicGet("myQueue", false);  //客户端主动的从服务器拿消息,如果自动确认为false，客户端拿到消息后服务端不会删除该消息
        byte[] body = response.getBody();
        Envelope envelope = response.getEnvelope();
        System.out.println("接收到的消息："+ new String(body));

        channel.basicAck(envelope.getDeliveryTag(), false); //ack确认

    }

    /**
     * 拒绝消息
     * @param channel
     * @throws IOException
     */
    public static void rejectTest(final Channel channel) throws IOException {

        /*GetResponse response = channel.basicGet("myQueue", false);
        Envelope envelope = response.getEnvelope();*/
        /*channel.basicReject(envelope.getDeliveryTag(),true);
        System.out.println("被拒绝的消息是:"+new String(response.getBody()));*/

        channel.basicConsume("myQueue", false, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println(consumerTag +"我接收到的消息："+new String(body));

                long deliveryTag = envelope.getDeliveryTag();

                //requeue(重新入队，当为true时，它会重新推送给消费者)
                channel.basicNack(deliveryTag,true,false);   //两个都为true 会死循环


            }
        });


//        channel.basicNack(envelope.getDeliveryTag(), true, false);

    }
}
