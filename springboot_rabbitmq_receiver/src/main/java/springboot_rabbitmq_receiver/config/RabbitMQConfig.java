package springboot_rabbitmq_receiver.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/21 6:29 下午
 * Description: 多敲多练
 **/
@Configuration
public class RabbitMQConfig {


    //配置一个direct的交换机
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("bootDirectExchange");
    }
    //配置一个queue
    @Bean
    public Queue directQueue(){
        return new Queue("bootQueue");
    }
    /**
     * 配置一个队列和交换机的绑定
     * directQueue 需要绑定的队列的对象，参数名必须要个某个bean的方法名完全形同，这样就会自动进行注入
     */
    @Bean
    public Binding directBinding(Queue directQueue, DirectExchange directExchange){
        //完成绑定  参数1：需要绑定的队列     参数2；需要绑定的交换机    参数3：需要绑定的RoutingKey
        return BindingBuilder.bind(directQueue).to(directExchange).with("bootRoutignKey");
    }
}
