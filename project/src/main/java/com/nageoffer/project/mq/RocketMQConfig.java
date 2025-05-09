package com.nageoffer.project.mq;


//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RocketMQConfig {
//    @Value("${rocketmq.name-server}")
//    private String nameSrv;
//
//    @Autowired
//    private ApplicationContext ctx;   // 方便 Spring 生命周期管理
//
//    @Bean("createTemplate")
//    public RocketMQTemplate createTemplate() throws MQClientException {
//        DefaultMQProducer producer = new DefaultMQProducer("${second-producer.producer");
//        producer.setNamesrvAddr(nameSrv);
//        producer.setSendMsgTimeout(2000);
//        producer.setRetryTimesWhenSendFailed(1);
//        //producer.start(); // 启动生产者
//
//        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
//        rocketMQTemplate.setProducer(producer);
//        return rocketMQTemplate;
//    }
//}
