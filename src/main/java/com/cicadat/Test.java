package com.cicadat;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Test {


    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtils.createConnection();
        Channel channel = connection.createChannel();
//        ExchangeTest.exchangeDirect(channel);
//        ExchangeTest.exchangeFanout(channel);
//        ExchangeTest.exchangeTopic(channel);
//        ExchangeTest.exchangeNoWait(channel);
//        ExchangeTest.exchangeAutoDelete(channel);

//        ExchangeTest.exchangeInner(channel, connection);

//        PublishTest.pulishForMandatory(channel);

        PublishTest.publish(channel);
        /*connection.addShutdownListener(new ShutdownListener() {
            public void shutdownCompleted(ShutdownSignalException cause) {
                System.out.println("cause"+cause);
            }
        });*/   //添加connection关闭监听器

        TimeUnit.SECONDS.sleep(1);
        channel.close();
        connection.close();
    }


    //交换器特性测试
    static class ExchangeTest {
        /**
         * Exchange Direct类型
         *
         * 发送的消息到绑定的指定路由的队列里， 可以发送多个队列(只要这个队列绑定这个交换器，且路由一致)
         * @param channel
         */
        public static void exchangeDirect(Channel channel) throws Exception{

            //声明交换器
            channel.exchangeDeclare("myExchange", BuiltinExchangeType.DIRECT);

            //声明两个队列
            channel.queueDeclare("myQueue", false, false, false, null);
            channel.queueDeclare("myQueue2", false, false, false, null);

            //交换器与两个队列绑定，且绑定路由一样
            channel.queueBind("myQueue","myExchange","routingKey");
            channel.queueBind("myQueue2","myExchange","routingKey");
            //channel.queueBind("myQueue2","myExchange","routingKey1");

            //向指定的路由向交换器发送消息
            channel.basicPublish("myExchange","routingKey", null, "hello test".getBytes());
        }

        /**
         *交换器 Fanout
         * 该类型交换器跟路由键没关系， 只要跟该交换器绑定的队列均可接收到消息
         * @param channel
         * @throws Exception
         */
        public static void exchangeFanout(Channel channel) throws Exception{

            //声明交换器
            channel.exchangeDeclare("myExchange", BuiltinExchangeType.FANOUT);

            //声明两个队列
            channel.queueDeclare("myQueue", false, false, false, null);
            channel.queueDeclare("myQueue2", false, false, false, null);

            //交换器与两个队列绑定，且绑定路由一样
            channel.queueBind("myQueue","myExchange","routingKey");
            channel.queueBind("myQueue2","myExchange","routingKey123");

            //向指定的路由向交换器发送消息
            channel.basicPublish("myExchange","routingKey", null, "hello test".getBytes());
        }

        /**
         * Topic交换器
         * 发送消息的匹配带"#"或"*"的路由键， 其中"#"代表多个单词，"*"代表一个单词，单词用"."区分
         *
         * @param channel
         * @throws Exception
         */
        public static void exchangeTopic(Channel channel) throws Exception{

            //声明交换器
            channel.exchangeDeclare("myExchange", BuiltinExchangeType.TOPIC);

            //声明两个队列
            channel.queueDeclare("debug", false, false, false, null);
            channel.queueDeclare("info", false, false, false, null);
            channel.queueDeclare("error", false, false, false, null);

            //交换器与两个队列绑定，且绑定路由一样
            channel.queueBind("debug","myExchange","log.debug");
            channel.queueBind("info","myExchange","log.*");
            channel.queueBind("error","myExchange","log.#");

            //向指定的路由向交换器发送消息
            //channel.basicPublish("myExchange","log.all", null, "hello test".getBytes());  // error 和info会接收
            channel.basicPublish("myExchange","log.all.error", null, "hello test".getBytes());  //error会接收
            channel.basicPublish("myExchange","log.debug", null, "hello test".getBytes());  //debug ,info, error 都能接收
        }

        /**
         * 这个后面再补 todo
         * @param channel
         */
        public static void exchangeHeader(Channel channel){

        }


        /**
         *客户端不用等服务器返回 (
         * 服务端不用给客户端返回 "Exchange.Declare-Ok", 正常情况客户端发送一个声明交换器的请求，服务端创建成功会返回一个创建成功的Method,具体可以抓包工具看
         * @param channel
         * @throws Exception
         */
        public static void exchangeNoWait (Channel channel) throws Exception{
            channel.exchangeDeclareNoWait("myExchange", "direct", false, false, false, null);
            channel.queueDeclare("myQueue",false, false, false, null);
            channel.queueBind("myQueue","myExchange","routingKey");
            channel.basicPublish("myExchange","routingKey", null, "hello noWait".getBytes());
        }

        /**
         * 自动删除,只有与该交换器绑定的所有队列都解绑才会删除， 否则不会删除
         *
         * 当交换器与交换器都声明为自动删除且绑定时，当解绑的时候，只有作为源头的交换器才会自动删除，而目地的那个交换器不会自动删除
         * @param channel
         * @throws Exception
         */
        public static void exchangeAutoDelete(Channel channel) throws Exception{
            channel.exchangeDeclare("myExchange", "direct", false, true, false, null);

            channel.exchangeDeclare("myExchange2", "direct", false, true, false, null);

            channel.exchangeBind("myExchange","myExchange2","routingKey");
            /*channel.queueDeclare("myQueue",false, false, false, null);

            channel.queueBind("myQueue","myExchange","routingKey");*/
            channel.basicPublish("myExchange","routingKey", null, "hello noWait".getBytes());
            //channel.exchangeUnbind("myExchange2","myExchange","routingKey");    //如果解绑时交换器的顺序反的服务器会报错，但客户端不会报错
            channel.exchangeUnbind("myExchange","myExchange2","routingKey");    //myExchange2 会自动删除myExchange 不会删除
        }

        /**
         * 内置交换器,不能直接发送消息到内置交换器，如果直接发送消息到内置交换器，服务端会主动关闭当前Channel，并返回错误消息
         * 如果想往内置交换器绑定的队列发送消息，只能向内置交换器绑定的交换器发送消息，再把消息路由到内置交换器
         * @param channel
         * @throws Exception
         */
        public static void exchangeInner(Channel channel, Connection connection) throws Exception{

            channel.exchangeDeclare("myExchange", "direct", false, false, true, null);

            channel.queueDeclare("myQueue",false, false, false, null);

            channel.queueBind("myQueue","myExchange","routingKey");

            /*try {
                channel.basicPublish("myExchange","routingKey", null, "hello inner".getBytes());
                channel.exchangeDeclare("test","direct");   //测试当向内置交换器发送消息的时候，捕获异常再在当前channel操作,还是会报错
            } catch (IOException e) {
                //e.printStackTrace();
            }
            Channel channel2 = connection.createChannel();
            channel2.exchangeDeclare("test","direct");
            channel2.queueDeclare("testQueue",false, false, false, null);
            channel2.queueBind("testQueue","test","testKey");
            channel2.basicPublish("test","testKey", null, "haha 我还能执行".getBytes());
            System.out.println("haha");
            channel2.close();   这时channel 不能关闭了(否则报错)，因为服务端已经主动关闭了
            */

            channel.exchangeDeclare("myExchangeOuter", "direct", false, false, false, null);

            channel.exchangeBind("myExchange","myExchangeOuter", "routingKey");

            channel.queueBind("myQueue","myExchange", "routingKey");

            channel.basicPublish("myExchangeOuter","routingKey", null, "hello inner".getBytes());



        }

    }


    //发送消息测试，从抓包中可以看到，消息由三部分组成：1.消息元数据 2.消息header 3.消息body
    static class PublishTest{


        /**
         * 当mandatory设置为true时，且发送的消息没有找到指定的队列时，消息将返回到ReturnListener
         * @param channel
         * @throws Exception
         */
        public static void pulishForMandatory(Channel channel) throws Exception{

            channel.exchangeDeclare("myExchange", BuiltinExchangeType.DIRECT);

            channel.queueDeclare("myQueue", false, false, false, null);

            channel.queueBind("myQueue","myExchange","routingKey");

            //mandatory(强制的)
            channel.basicPublish("myExchange","routingKey", true, null, "hello test".getBytes());

            channel.addReturnListener(new ReturnListener() {

                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {

                    System.out.println(replyText);
                    System.out.println("这是没有发送到队列的消息"+ new String(body));
                }
            });
        }

        public static void publish(Channel channel) throws Exception{
            channel.exchangeDeclare("myExchange", BuiltinExchangeType.DIRECT);

            channel.queueDeclare("myQueue", false, false, false, null);

            channel.queueBind("myQueue","myExchange","routingKey");

            //mandatory(强制的)
//            channel.basicPublish("myExchange","routingKey", false, null, "hello test 0".getBytes());

            for (int i = 1; i < 100; i++){
                channel.basicPublish("myExchange","routingKey", false, null, ("hello test"+i).getBytes());
            }
        }
    }

}
