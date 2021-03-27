package cn.jzfw;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2020/9/11 2:59 下午
 * Description: 多敲多练
 **/
public class Send {
    public static void main(String[] args) {
        //创建连接工厂对象
        ConnectionFactory factory = new ConnectionFactory();
        //配置Rabbit连接相关信息
        factory.setHost("127.0.0.1");//指定端口
        factory.setPort(5672);//指定端口
        factory.setUsername("root");//指定账号
        factory.setPassword("root");//指定密码
        Connection connection = null; //定义连接
        Channel channel = null;//定义通道
        try {
            connection = factory.newConnection();//获取连接
            channel = connection.createChannel();//获取通道
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
            channel.queueDeclare("myQueue",true,false,false,null);

            String message = "我的RabbitMq的测试消息2";
            /**
             * 发送消息到MQ
             * 参数1 为交换机名称   这里为空字符窜表示不使用交换机
             * 参数2 为队列名称或routingkey，当指定了交换机名称以后这个值就是Routingkey
             * 参数3 为消息属性信息 通常为空即可
             * 参数4 为具体的消息的字节数组
             * 注意：队列名必须要与接受时完全一致
             */
            channel.basicPublish("","myQueue",null,message.getBytes("utf-8"));

            System.out.println("消息发送成功");

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
