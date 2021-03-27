package springboot_rabbitmq_send.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springboot_rabbitmq_send.service.impl.SendServiceImpl;

@SpringBootTest
class SpringbootRabbitmqSendApplicationTests {
    @Autowired
    private SendServiceImpl sendService;

    @Test
    void contextLoads() {
        sendService.sendMessage("boot的测试数据2");
    }

    @Test
    void test01(){
        sendService.sendFanoutMessage("fanout的测试数据");
    }
    @Test
    void test02(){
        sendService.sendTopicMessage("fanout的测试数据");
    }


}
