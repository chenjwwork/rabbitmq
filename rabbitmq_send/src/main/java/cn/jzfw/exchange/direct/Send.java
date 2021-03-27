package cn.jzfw.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2020/9/23 9:37 上午
 * Description: 多敲多练
 **/
public class Send {
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
            channel.queueDeclare("myDirectQueue",true,false,false,null);
            /**
            * 创建交换机
             * 参数1 为交换机的名称任意取值
             * 参数2 为交换机的类型 取值为direct fanout topic headers
             * 参数3 为是否为持久化交换机
             * 注意：
             *        1、声明交换机时入如果这个交换机已经存在则会放弃声明，如果不存在则会声明交换机
             *        2、这个代码是可有可无，但是在使用的时候必须确保这个交换机被声明
             **/
            channel.exchangeDeclare("directExchange","direct",true);
            /**
             * 将队列绑定到交换机
             * 参数1 为队列名称
             * 参数2 为交换机名称
             * 参数3 为消息的Routingkey（就是Bindingkey）
             * 注意：
             *      1、在进行队列和交换机绑定时必须确保交换机和队列已经成功的声明
             */
            channel.queueBind("myDirectQueue","directExchange","directRoutingKey");

            /**
             * 发送消息
             * 参数1 交换机名称
             * 参数2 为消息的RoutingKey，如果这个消息的Routingkey和某个队列与交换机的RoutingKey一致那么
             *        这个消息就会发送的指定的队列中
             */
            String message = "derect的测试消息";
            channel.basicPublish("directExchange","directRoutingKey",null,message.getBytes("utf-8"));
            System.out.println("direct消息发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
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

