package com.cicadat;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * 交换器的参数和使用测试
 */
public class ExchangeTest {

    public static void main(String[] args) throws Exception {
        Channel channel = ConnectionUtils.createChannel();

        //自动删除，只有执行了解绑动作且全部解解绑才会自动删除
        //内置交换器不能直接向其发送消息
        channel.exchangeDeclare("myExchange", BuiltinExchangeType.DIRECT, false, true,false, null);
        /*channel.exchangeDeclare("myExchange2","direct", false, false, null);
        channel.exchangeDeclare("myExchange3","direct", false, false, null);
        channel.exchangeBind("myExchange2","myExchange","exchangeRoute");   //交换器与交换器互相绑定
        channel.exchangeBind("myExchange3","myExchange","exchangeRoute");
        channel.exchangeUnbind("myExchange2","myExchange","exchangeRoute");*/
        channel.basicPublish("myExchange","routingKey",null,"hello inner".getBytes());


        ConnectionUtils.release();
    }
}
