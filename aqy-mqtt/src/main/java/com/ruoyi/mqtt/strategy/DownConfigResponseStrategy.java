package com.ruoyi.mqtt.strategy;

import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.domain.aqy.AqyMqttCmdMessage;
import com.ruoyi.service.IAqyMqttCmdMessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author：MXJ
 * @Date：2024/10/11 16:41
 */
@Slf4j
@Component
public class DownConfigResponseStrategy implements MessageHandlerStrategy{
    @Autowired
    private IAqyMqttCmdMessageService mqttCmdMessageService;

    @Override
    public void handle(String payload) {
        CmdResultData bean = JSONUtil.toBean(payload, CmdResultData.class);
        if(bean != null) {
            AqyMqttCmdMessage mqttCmdMessage = mqttCmdMessageService.selectAqyMqttCmdMessageByMsgId(bean.getMsgId(), bean.getCmd());
            mqttCmdMessage.setReplyRest("网关下发结果：" + bean.getData().isRet());
            mqttCmdMessageService.updateAqyMqttCmdMessage(mqttCmdMessage);
        }
    }

    @Override
    public String getTopicPattern() {
        return "\\$reply/.*/downConfig";
    }

    private String topic;

    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }
}

@Data
class CmdResultData {
    private String cmd;

    private CmdResultDownConfig data;

    private Integer msgId;
}

@Data
class CmdResultDownConfig{
    private boolean ret;

    private Integer errorCode;
}
