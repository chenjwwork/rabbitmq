package springboot_rabbitmq_receiver.service;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/21 6:13 下午
 * Description: 多敲多练
 **/
public interface ReceiveService {
    void receive();
    void directReceive(String message);
    void fanoutReceive01(String message);
    void fanoutReceive02(String message);
}
