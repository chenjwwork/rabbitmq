package cn.jzfw.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/20 6:11 下午
 * Description: 多敲多练
 **/
public class Send02 {
    public static void main(String[] args) {

        //创建工厂
        ConnectionFactory factory = new ConnectionFactory();

        //配置连接信息
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("root");
        factory.setPassword("root");

        Connection connection = null;
        Channel channel = null;


        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            //由于fanout类型交换机，因此消息的接收方肯定又一个或多个，因此不建议在消息发送时创建队列
            //以及绑定交换机，建议在消费者中创建队列并绑定消费者
            //但是发送消息时要确保交换机和队列存在
            channel.queueDeclare("myFanoutQueue",true,false,false,null);

            channel.exchangeDeclare("fanoutExchange","fanout",true);

            channel.queueBind("myFanoutQueue","fanoutExchange","");
            /**
             * 发送消息
             * 参数1 交换机名称
             * 参数2 为消息的RoutingKey，如果这个消息的Routingkey和某个队列与交换机的RoutingKey一致那么
             *        这个消息就会发送的指定的队列中
             */
            String message = "fanout02的测试消息";
            channel.basicPublish("fanoutExchange","",null,message.getBytes("utf-8"));
            System.out.println("fanout02消息发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if(channel!=null){
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
