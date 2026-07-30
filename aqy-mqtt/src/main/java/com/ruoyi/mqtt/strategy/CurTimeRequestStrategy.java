package com.ruoyi.mqtt.strategy;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.mqtt.enums.CommandMessageType;
import com.ruoyi.mqtt.model.IotData;
import com.ruoyi.mqtt.sender.CmdMqttSender;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 智能网关请求更新时间指令
 * @Author：MXJ
 * @Date：2024/10/11 16:41
 */
@Slf4j
@Component
public class CurTimeRequestStrategy implements MessageHandlerStrategy {
    @Resource
    private CmdMqttSender cmdMqttSender;

    @Override
    public void handle(String payload) {
        log.info("CurTimeRequestStrategy收到消息:" + payload);
        IotData iotData = new IotData(getGatwayNo(), CommandMessageType.GET_CURRENT_IMG.getKeyCode());
        cmdMqttSender.setCurrentTimeResponse(iotData, DateUtils.getTime());
    }

    @Override
    public String getTopicPattern() {
        return "\\$request/.*/getCurrentTime";
    }

    private String topic;

    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }

    private String getGatwayNo() {
        if (StringUtils.isNotEmpty(this.topic)) {
            String[] splits = this.topic.split("/");
            return splits[1];
        } else
            return null;
    }
}
