package com.nageoffer.project.mq;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

@ExtRocketMQTemplateConfiguration(
        nameServer = "${rocketmq.name-server}",      // 也可写死
        group = "${second-producer.group}",
        enableMsgTrace = true,                      // 可打开消息轨迹
        customizedTraceTopic = "TRACE_TOPIC"
)
public class CreateRocketMQTemplate extends RocketMQTemplate {
}
