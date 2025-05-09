package com.nageoffer.project.mq.producer;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortLinkCreateProducer  {

//    @Resource()
//    private final CreateTemplate rocketMQTemplate;

    //注意bean名默认小写
    @Resource(name = "createRocketMQTemplate")
    private final RocketMQTemplate rocketMQTemplate;


//    @Value("short-link_producer-service-create")
//    private String topic;

    public void send(Map<String, String> producerMap) throws MQClientException {

        String topic = "short-link_producer-service-create";
        String keys = UUID.randomUUID().toString();
        producerMap.put("keys", keys);
        Message<Map<String, String>> build = MessageBuilder
                .withPayload(producerMap)
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .build();
        SendResult sendResult;
        try {
            sendResult = rocketMQTemplate.syncSend(topic, build, 2000L);
            log.info("[短连接创建消息] 消息发送结果：{}，消息ID：{}，消息Keys：{}， topic：{}",
                    sendResult.getSendStatus(), sendResult.getMsgId(), keys, topic);
        } catch (Throwable ex) {
            log.error("[短连接创建消息] 消息发送失败，消息体：{}", JSON.toJSONString(producerMap), ex);
            // 自定义行为...
        }
    }
}
