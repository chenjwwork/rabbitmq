package cn.jzfw.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2020/9/23 10:12 上午
 * Description: 多敲多练
 **/
public class Receive {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root");

        Connection connection = null;
        Channel channel = null;


        try {
            connection = factory.newConnection();

            channel = connection.createChannel();

            //下面3行代码可有可无
            channel.queueDeclare("myDirectQueue", true, false, false, null);
            channel.exchangeDeclare("directExchange", "direct", true);
            channel.queueBind("myDirectQueue", "directExchange", "directRoutingKey");


            channel.basicConsume("myDirectQueue", true, new DefaultConsumer(channel) {

                //监听某个队列并获取队列中的数据
                //注意：当前被监听的队列必须已经存在并正确的绑定到某个交换机
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);
                    String message = new String(body, "utf-8");
                    System.out.println("消费者:" + message);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
