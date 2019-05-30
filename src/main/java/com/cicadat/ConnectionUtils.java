package com.cicadat;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtils {

    private static Connection connection = null;

    private static Channel channel = null;

    public static Channel createChannel() throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.56.101");
        connectionFactory.setUsername("rabbitstudy");
        connectionFactory.setPassword("123456");
        //创建连接
        connection = connectionFactory.newConnection();
        //创建信道
        channel = connection.createChannel();
        return channel;
    }


    public static void release(){
        if (channel != null){
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        if (connection != null){
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
