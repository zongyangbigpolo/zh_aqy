package com.ruoyi.web.controller.mqtt;

import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.mqtt.model.IotData;
import com.ruoyi.mqtt.properties.TopicConstant;
import com.ruoyi.mqtt.sender.IMqttSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/10/21 11:25
 */
@RestController
@RequestMapping("/aqy/mqtt")
@Api( value = "MQTT接口", tags = "MQTT接口")
public class MqttController2 {


    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender mqttSender;

    @ApiOperation("测试发送")
    @RequestMapping("/sendMessage")
    public String testMqtt(Integer qos, String topic, String msg) {
        mqttSender.sendToMqtt(msg);
        mqttSender.sendToMqtt(topic, msg);
        mqttSender.sendToMqtt(topic, qos, msg);
        return "ok";
    }

    @PostMapping("/control-command")
    public AjaxResult controlCommand(@RequestBody IotData iotData) {
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_DATA_RESP.replace("${deviceIdentity}", deviceId);
        String payload = JSONUtil.toJsonStr(iotData);
        mqttSender.sendToMqtt(topic, payload);
        System.out.println("发送成功=>" + "主题：" + topic + "  载荷:" + payload);
        return AjaxResult.success("发送成功");
    }

    @PostMapping("/dataSubscribe")
    public AjaxResult dataSubscribe(@RequestBody IotData iotData) {
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_DATA_SUBSCRIBE.replace("${deviceIdentity}", deviceId);
        String payload = JSONUtil.toJsonStr(iotData);
        mqttSender.sendToMqtt(topic, payload);
        System.out.println("发送成功=>" + "主题：" + topic + "  载荷:" + payload);
        return AjaxResult.success("发送成功");
    }

}
