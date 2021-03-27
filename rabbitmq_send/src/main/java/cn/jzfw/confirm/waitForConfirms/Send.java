package cn.jzfw.confirm.waitForConfirms;

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
            //阻塞线程等待服务器返回响应，用于是否消费返回成功，如果服务确认消费已经发送成功则返回true，否则返回false
            //可以为这个方法设置一个毫秒用于确认我们需要等待服务确认的超时时间
            //如果超过了指定时间以后则会抛出InterupedException表示服务器出现了问题需要补发
            //将消息缓存到redis后利用定时任务补发
            //无论是返回false还是抛出异常，都有可能发送成功又可能没有发送成功
            //如果我们一定要求这个消息发送到队列，我们可以采用消息补发
            //所谓补发就是重新发送一一遍，可以使用递归或redis+定时任务来完成
            Boolean falg = channel.waitForConfirms();
            System.out.println("发送者确认模式消息发送成功!"+falg);
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
