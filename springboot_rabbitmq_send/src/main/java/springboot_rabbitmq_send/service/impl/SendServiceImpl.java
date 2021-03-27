package springboot_rabbitmq_send.service.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import springboot_rabbitmq_send.service.SendService;

import javax.annotation.Resource;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/21 6:14 下午
 * Description: 多敲多练
 **/
@Service("sendService")
public class SendServiceImpl implements SendService {
    //注入AMQP的模版类,利用这个队形来发送和接收消息
    @Resource
    private AmqpTemplate amqpTemplate;



    @Override
    public void sendMessage(String message) {
        //参数1为交换机名，参数2为RoutingKey，参数3为我们具体发送的消息数据
        amqpTemplate.convertAndSend("bootDirectExchange","bootRoutignKey",message);
    }

    @Override
    public void sendFanoutMessage(String message) {
        amqpTemplate.convertAndSend("fanoutExchange","",message);
    }

    @Override
    public void sendTopicMessage(String message) {
        amqpTemplate.convertAndSend("topicbootExchange","aa",message);
    }
}
