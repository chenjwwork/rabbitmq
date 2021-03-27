package cn.jzfw;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2020/9/11 3:49 下午
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
            //声明一个队列
            /**
             * 声明一个队列
             * 参数1 为队列名任意取之
             * 参数2 为 是否为持久化队列
             * 参数3 为 是否排外 如果排外则这个队列只允许一个消费者监听
             * 参数4 是否自动删除队列，如果为true则表示当队列中没有消息，也没有消费者连接时就会自动删除这个队列
             * 参数5 为队列的一些属性设置，通常为null即可
             * 注意：
             *      1、声明队列时，如果队列名称如果已经存在，则放弃声明，如果队列不存在则会声明一个新的队列
             *      2、队列名可以任意取值，但是要与接受时完全一致
             *      3、这行代码是可有可无，但一定要发送消息前确认队列名已经存在rabbitmq中，否则就会出现问题
             */
            channel.queueDeclare("myQueue", true, false, false, null);


            /**
             * 接受消息
             * 参数1 为当前消费者需要监听的消息，队列名要与发送队列名一样
             * 参数2 为消息是否自动确认，true自动确认，接受完消息以后会自动将消息从队列中移除
             * 参数3 为消息接受者的标签，用于当多个消费者同时监听同一个队列时用于区分不同消费者，通常为空字符串即可
             * 参数4 为消息接受的回调方法，这个方法中具体完成对消息的代码处理
             * 使用了basicConsumef方法以后，会启动一个线程在持续的监听队列，如果队列中有信息数据则会自动接收
             * 因此不能不能关闭连接和通道对象
             */
            channel.basicConsume("myQueue", true, "", new DefaultConsumer(channel) {
                //消息的具体接收和处理方法

                /**
                 参数1 标签
                 参数2 消息对象
                 参数3 属性信息
                 参数4 消息数据
                 */
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);
                    String message = new String(body, "utf-8");
                    System.out.println("消息:" + message);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
