package cn.jzfw.exchange.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/20 6:47 下午
 * Description: 多敲多练
 **/
public class Receive01 {

    public static void main(String[] args) {

        //创建工厂
        ConnectionFactory factory = new ConnectionFactory();
        //配置连接工厂信息
        factory.setHost("127.0.0.1");//指定端口
        factory.setPort(5672);//指定端口
        factory.setUsername("root");//指定账号
        factory.setPassword("root");//指定密码
        Connection connection = null; //定义连接
        Channel channel = null;//定义通道

        try {
            connection = factory.newConnection();//获取连接
            channel = connection.createChannel();//获取通道

            channel.queueDeclare("topicQueue01", true, false, false, null);
            channel.exchangeDeclare("topicExchange", "topic", true);
            channel.queueBind("topicQueue01", "topicExchange", "aa");


            channel.basicConsume("topicQueue01", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);
                    String string = new String(body, "utf-8");
                    System.out.println("topic01消费者aa" + string);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
