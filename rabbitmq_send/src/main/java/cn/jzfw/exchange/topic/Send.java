package cn.jzfw.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/20 6:47 下午
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


            channel.exchangeDeclare("topicExchange","topic",true);

            String message = "topic测试消息";
            channel.basicPublish("topicExchange","aa.bb.cc.dd",null,message.getBytes("utf-8"));
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
                } finally {
                }
            }
        }

    }
}

