package cn.jzfw.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/21 5:53 下午
 * Description: 多敲多练
 **/
public class Repetition {
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

                    boolean isRedeliver = envelope.isRedeliver();
                    /**
                     * 获取当前消息是否被接受过一次，返回值false则表示没有被接受过，
                     * true表示之前被接收过，可能也处理完成，因此我们要进行消息的防重复处理
                     */
                    if (!isRedeliver) {
                        String message = new String(body, "utf-8");
                        System.out.println("消费者处理了消息:" + message);
                        //获取我们消息的编号，我们需要根据消息的编号来进行确认
                        long d = envelope.getDeliveryTag();
                        Channel c = super.getChannel();
                        //手动确认消息，确认消息以后表示当前消息已经成功处理，需要从队列中移除掉，
                        // 这个方法应该在当前消息的处理程序全部完成以后再执行
                        //参数1 为消息的编号
                        //参数2 表示是否确认多个，
                        // 如果为true，表示需要确认小等于当前标号的全部消息
                        //如果为false，表示只需确认当前编号消息
                        c.basicAck(d, true);
                    } else {
                        /**
                         * 到了这里，表示这个消息之前被接受过，需要进行防重复处理，
                         * 例如查询数据库中是否已经添加或修改了记录
                         * 经过判断这条消息没有被处理则需要重新处理并确认这条消息
                         * 如果已经处理了，则直接确认消息即可无需其他操作
                         */

                    }
                    Channel c = super.getChannel();

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
