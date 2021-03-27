package cn.jzfw.exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/20 5:13 下午
 * Description: 多敲多练
 **/
public class Receive01 {
    public static void main(String[] args) {

        //创建工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //配置连接信息
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");

        Connection connection = null;
        Channel channel = null;


        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();


            /*由于fanout类型的交换机的消息类似与广播模式，他不需要绑定bingkey
            而又有可能同事有多个消费者同事接受这个交换机的消息，所以我们创建队列时要创建一个随机的队列名称
            没有参数的queueDeclare方法会随机创建一个队列
            这个队列是非持久性的
            是排外的（同时最多又一个消费者监听当前队列）
            自动删除的，当没有队列监听时这个队列会自动删除
            getQueue()获取这个随机的名称
            */
            //创建队列，fanout所以省略
            String queueName = channel.queueDeclare().getQueue();
            //创建交换机
            channel.exchangeDeclare("fanoutExchange", "fanout", true);
            //将这个随机的队列绑定到交换机中，
            // 由于是fanout类型交换机不需要制定ROutingKey进行绑定
            channel.queueBind(queueName, "fanoutExchange", "");

            //接受消息
            channel.basicConsume(queueName, true, "", new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);
                    String string = new String(body, "utf-8");
                    System.out.println("Reveive01消费者" + string);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
