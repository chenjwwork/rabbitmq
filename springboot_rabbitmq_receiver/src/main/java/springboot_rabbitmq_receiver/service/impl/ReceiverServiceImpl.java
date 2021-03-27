package springboot_rabbitmq_receiver.service.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import springboot_rabbitmq_receiver.service.ReceiveService;


import javax.annotation.Resource;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/21 6:14 下午
 * Description: 多敲多练
 **/
@Service("receiveService")
public class ReceiverServiceImpl implements ReceiveService {
    //注入AMQP的模版类,利用这个队形来发送和接收消息
    @Resource
    private AmqpTemplate amqpTemplate;


    /**
     * 这个接收不是不间断接收，没执行一次方法只能接收一次消息，
     * 如果有新消息进入，不会自动接收
     */
    @Override
    public void receive() {
        String  message=(String)amqpTemplate.receiveAndConvert("bootQueue");
        System.out.println(message);
    }

    /**
     * @RabbitListener()标记当前方法是一个消息监听方法，作用是持续性接收消息
     * 这个方法不需要调用，Spring会自动运行这个监听
     * message:要接收的消息
     * queues：用于指定一个已经存在的队列名称，用于进行监听
     *
     * 如果当前方法正常结束，Spring就会自动确认消息，出现异常则不会自动确认
     * 因此在消息处理时，我们需要做好消息的防重复处理工作
     */
    @RabbitListener(queues = "bootQueue")
    public void directReceive(String message){
        System.out.println("监听器"+  message);

    }



    @RabbitListener(bindings = {//@QueueBinding此注解完成队列和交换机的绑定
                                @QueueBinding(value = @Queue(),//@Queue创建一个队列（没有指定参数则表示一个随机队列）
                                              exchange = @Exchange(name="fanoutExchange",type = "fanout"))})//创建一个交换机)
    public void fanoutReceive02(String message){
        System.out.println("fanoutReceive02监听器"+  message);

    }

    @RabbitListener(bindings = {//@QueueBinding此注解完成队列和交换机的绑定
            @QueueBinding(value = @Queue(),//@Queue创建一个队列（没有指定参数则表示一个随机队列）
                    exchange = @Exchange(name="fanoutExchange",type = "fanout"))})//创建一个交换机)
    public void fanoutReceive01(String message){
        System.out.println("fanoutReceive01监听器"+  message);
    }
    @RabbitListener(bindings = @QueueBinding(value = @Queue("topicboot01"), exchange = @Exchange(name="topicbootExchange",type = "topic"),key = "aa"))
    public void topicReceive01(String message){
        System.out.println("topic消费者--aa---"+message);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue("topicboot02"), exchange = @Exchange(name="topicbootExchange",type = "topic"),key = "aa.*"))
    public void topicReceive02(String message){
        System.out.println("topic消费者--aa.*---"+message);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue("topicboot03"), exchange = @Exchange(name="topicbootExchange",type = "topic"),key = "aa.#"))
    public void topicReceive03(String message){
        System.out.println("topic消费者--aa.#---"+message);
    }
}