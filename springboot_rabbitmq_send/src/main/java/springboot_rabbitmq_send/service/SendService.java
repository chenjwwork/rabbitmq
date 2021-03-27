package springboot_rabbitmq_send.service;

/**
 * The company is 鉴真防务
 * User: 陳佳伟
 * Date: 2021/3/21 6:13 下午
 * Description: 多敲多练
 **/
public interface SendService {
    void sendMessage(String message);


    void sendFanoutMessage(String message);

    void sendTopicMessage(String message);
}
