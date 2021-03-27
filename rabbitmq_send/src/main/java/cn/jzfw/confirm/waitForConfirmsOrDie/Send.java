package cn.jzfw.confirm.waitForConfirmsOrDie;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/21 1:35 下午
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

            //声明队列  队列名 是否持久化 是否排外 是否删除 属性
            channel.queueDeclare("confirmQueue",true,false,false,null);
            channel.exchangeDeclare("directConfirmExchange","direct",true); //声明交换机     交换机名 交换机类型  是否持久化
            channel.queueBind("confirmQueue","directConfirmExchange","confirmRoutingKey");//绑定交换机
            String message = "发送者确认模式测试消息";
            //启用发送者确认模式
            channel.confirmSelect();
            //发送消息  交换机名称
            channel.basicPublish("directConfirmExchange","confirmRoutingKey",null,message.getBytes("utf-8"));
            /**
             * 批量消息确认，他会同时向服务中确认之前当前通道中发送的所有消息是否被全部写入
             * 这个方法没有任何的返回值，如果服务区中有一条消息没有成功或向服务器发送确认时服务不可访问时都会被认为
             * 消息确认失败，可能有消息没有发送成功，我们需要消息的补发
             * 如果无法向服务器获取确认信息，那么当前方法就会跑出InterruptedException异常，这是就需要补发消息到队列
             * waitForConfirmsOrDie这个方法可以设置一个参数timeout，用于等待服务确认时间，如果超过这个时间也会跑出异常
             * 表示确认失败，需要补发消息
             * 注意：
             *   批量确认消息要比普工确认消息要快，但是如果出现了需要消息补发的情况，我们不能确认具体时那条消息
             *   没有发送，需要将本次发送的消息全部补发
             */
            channel.waitForConfirmsOrDie();
            System.out.println("发送者确认模式消息发送成功!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
