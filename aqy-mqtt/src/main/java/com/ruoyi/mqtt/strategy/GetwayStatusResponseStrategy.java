package com.ruoyi.mqtt.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author：MXJ
 * @Date：2024/10/11 16:41
 */
@Slf4j
@Component
public class GetwayStatusResponseStrategy implements MessageHandlerStrategy{
    @Override
    public void handle(String payload) {
        log.info("GetwayStatusResponseStrategy收到消息:" + payload);
    }

    @Override
    public String getTopicPattern() {
        return "$reply/.*/getStatus";
    }

    private String topic;

    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }
}
