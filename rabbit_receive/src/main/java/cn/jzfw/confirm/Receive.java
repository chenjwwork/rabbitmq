package cn.jzfw.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/21 3:31 下午
 * Description: 多敲多练
 **/
public class Receive {

    public static void main(String[] args) {
        final ConnectionFactory factory = new ConnectionFactory();
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
            channel.queueDeclare("confirmQueue", true, false, false, null);
            channel.exchangeDeclare("directConfirmExchange", "direct", true);
            channel.queueBind("confirmQueue", "directConfirmExchange", "confirmRoutingKey");

            //开启事物
            channel.txSelect();
            /**
             * 接受消息，
             * 参数2 为消息的确认机制，true表示自动消息确认，确认以后会自动从队列中移除
             *      如果为false，表示手动确认消息
             * 注意：
             *      如果我们只是接收消息但是还没有来得及处理，当前应用就崩溃或者处理时就像数据库中写，
             *      但是数据库这时不可用，由于消息是自动确认的，那么消息自动确认就会在接收完成以后从队列中剔除
             *      这就会丢失消息
             */
            channel.basicConsume("confirmQueue", false, "", new DefaultConsumer(channel) {

                //监听某个队列并获取队列中的数据
                //注意：当前被监听的队列必须已经存在并正确的绑定到某个交换机
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "utf-8");
                    System.out.println("消费者处理了消息:" + message);
                    //获取我们消息的编号，我们需要根据消息的编号来进行确认
                    long d = envelope.getDeliveryTag();
                    //获取当前通道内部类的通道
                    Channel c = super.getChannel();
                    //手动确认消息，确认消息以后表示当前消息已经成功处理，需要从队列中移除掉，
                    // 这个方法应该在当前消息的处理程序全部完成以后再执行
                    //参数1 为消息的编号
                    //参数2 表示是否确认多个，
                    // 如果为true，表示需要确认小等于当前标号的全部消息
                    //如果为false，表示只需确认当前编号消息
                    c.basicAck(d, true);
                    //如果启用事物，而消费者消费确认模式为手动确认那么必须提交事物，
                    // 否则即使调用了确认方法，那么消息也不会从队列中移除

                    c.txCommit();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
