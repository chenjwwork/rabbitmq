package springboot_rabbitmq_receiver.springboot_rabbitmq_receiver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springboot_rabbitmq_receiver.service.impl.ReceiverServiceImpl;

@SpringBootTest
class SpringbootRabbitmqReceiverApplicationTests {

    @Autowired
    private ReceiverServiceImpl receiverService;
    @Test
    void contextLoads() {
        receiverService.receive();
    }

}
