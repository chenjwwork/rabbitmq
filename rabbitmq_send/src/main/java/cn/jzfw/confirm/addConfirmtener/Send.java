package cn.jzfw.confirm.addConfirmtener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
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

            //启用发送者确认模式
            channel.confirmSelect();
            //发送消息  交换机名称


            /**
             * 异步确认消息监听器，需要在发送消息前启动
             */
            channel.addConfirmListener(new ConfirmListener() {
                //消息确认以后的回调方法

                /**
                 * 参数1被确认的消息的编号  自动递增用于当前消息市第多少个
                 * 参数2 当前消息是否被同时确认了多个 如果参数返回值为true，则表示同时确认了多条消息
                 * 消息等于当前参数1（消息编号）的所有消息全部确认
                 * 如果为false，则表示确认了当前编号的消息
                 * @param deliveryTag
                 * @param multiple
                 * @throws IOException
                 */
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("消息确认了---消息编号"+deliveryTag+",是否被确认了多条"+multiple);
                }

                //消息没有确认的回调方法
                //如果这个方法没执行，表示当前消息没有被确认，需要进行消息补发

                /**
                 * 参数1被没有确认的消息的编号  自动递增用于当前消息市第多少个
                 * 参数2 当前消息是否被同时没有确认多个
                 *      如果参数2 为true则表示小雨当前编号的所有消息可能都没有发送成功需要进行消息的补发
                 *      如果参数2 为falsez则表示d当前标号的消息没有发送成功，需要补发
                 * @param deliveryTag
                 * @param multiple
                 * @throws IOException
                 */
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("消息没有被确认---消息编号"+deliveryTag+",是否没有确认多条"+multiple);
                }
            });
            for (int i=1 ;i<=10000;i++){

                String message = "发送者确认模式测试消息"+i;
                channel.basicPublish("directConfirmExchange","confirmRoutingKey",null,message.getBytes("utf-8"));

            }

            System.out.println("发送者确认模式消息发送成功!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }  finally {
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
