package cn.jzfw.transaction;

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
            channel.queueDeclare("transactionQueue",true,false,false,null);
            //声明交换机     交换机名 交换机类型  是否持久化
            channel.exchangeDeclare("transactionExchange","direct",true);
            //绑定交换机
            channel.queueBind("transactionQueue","transactionExchange","transactionRoutingKey");

            String message = "transaction测试消息";
            //启动一个事物 启动事物以后所有写入队列中的消息
            //必须显示的调用txCommit()提交事物或者txRollback()回滚事物
            channel.txSelect();
            //发送消息  交换机名称
            channel.basicPublish("transactionExchange","transactionRoutingKey",null,message.getBytes("utf-8"));
            //提交事物，如果我们调用了txSelect()方法启动了事物，那么必须显示调用事物的提交
            //否则消息不会真正写入队列，提交时以后会讲内存中的消息写入队列并释放内存
            channel.txCommit();
            System.out.println("transaction消息发送成功!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if(channel!=null){
                try {
                    //回滚事物，放弃当前事物中所有没有提交的消息释放内存
                    channel.txRollback();
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
