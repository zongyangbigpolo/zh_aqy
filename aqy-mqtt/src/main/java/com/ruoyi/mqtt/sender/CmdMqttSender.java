package com.ruoyi.mqtt.sender;

import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.aqy.AqyMqttCmdMessage;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.UniqueIntGenerator;
import com.ruoyi.mqtt.enums.CommandMessageType;
import com.ruoyi.mqtt.model.*;
import com.ruoyi.mqtt.properties.TopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author：MXJ
 * @Date：2024/10/11 15:54
 */
@Component
@Slf4j
public class CmdMqttSender {
    /**
     * 注入发送MQTT的Bean
     */
    @Resource
    private IMqttSender mqttSender;

//    @Resource
//    private IAqyMqttCmdMessageService aqyMqttCmdMessageService;

    /**
     * 下载云平台配置到智能网关
     * @param iotData 智能网关设备编号信息
     * @param devList 测点传感器编码数组
     */
    public AjaxResult publisDownConfig(IotData iotData, String[] devList){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.DOWN_CONFIG.getKeyCode(), JSONUtil.toJsonStr(devList));
        CmdData cmdData = new CmdData();
        cmdData.setCmd("downConfig");
        cmdData.setData(new CmdDownConfigDev(devList));
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 设置智能传感器采集和上传参数
     * @param iotData 智能网关设备编号信息
     * @param devList 测点传感器编码数组
     * @return
     */
    public AjaxResult publishSampleParam(IotData iotData, CmdSampleParamDev[] devList){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.SET_SAMPLE_PARAM.getKeyCode(), JSONUtil.toJsonStr(devList));
        CmdData cmdData = new CmdData();
        cmdData.setCmd("setSampleParam");
        cmdData.setData(new CmdSampleParamDevList(devList));
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 设置智能传感器采集和上传参数
     * @param iotData 智能网关设备编号信息
     * @param devData 测点传感器数据
     * @return
     */
    public AjaxResult publishThresholdParam(IotData iotData, CmdSetThresholdParamDev devData){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.SET_THRESHOLD_PARAM.getKeyCode(), JSONUtil.toJsonStr(devData));
        CmdData cmdData = new CmdData();
        cmdData.setCmd("setThresholdParam");
        cmdData.setData(devData);
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 设置智能网关工作模式
     * @param iotData 智能网关设备编号信息
     * @param mode 智能网关工作模式
     * @return
     */
    public AjaxResult publishSetWorkMode(IotData iotData, Integer mode){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.SET_WORK_MODE.getKeyCode(), mode.toString());
        CmdData cmdData = new CmdData();
        cmdData.setCmd("setWorkMode");
        cmdData.setData(new CmdSetWorkMode(mode));
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 主动获取智能网关遥测信息
     * @param iotData 智能网关设备编号信息
     * @return
     */
    public AjaxResult publishSample(IotData iotData){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.SAMPLE.getKeyCode(), "");
        CmdData cmdData = new CmdData();
        cmdData.setCmd("sample");
        cmdData.setData(new CmdEmptyData());
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 更新智能网关时间
     * @param iotData 智能网关设备编号信息
     * @param curTime 系统当前时间
     * @return
     */
    public AjaxResult publishSetTime(IotData iotData, String curTime){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.SET_TIME.getKeyCode(), curTime);
        CmdData cmdData = new CmdData();
        cmdData.setCmd("setTime");
        cmdData.setData(new CmdSetTime(curTime));
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 响应智能网关更新时间请求
     * @param iotData 智能网关设备编号信息
     * @param curTime 系统当前时间
     * @return
     */
    public AjaxResult setCurrentTimeResponse(IotData iotData, String curTime){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_GET_CURRENT_TIME_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        CmdData cmdData = new CmdData();
        cmdData.setCmd("getCurrentTime");
        cmdData.setData(new CmdGetCurrentTime(curTime));
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 远程开启声光报警
     * @param iotData 智能网关设备编号信息
     * @param duration 报警持续时间
     * @return
     */
    public AjaxResult publishSoundLightAlarm(IotData iotData, Long duration){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", "cmd");
        Integer msgId = saveCmdMessage(CommandMessageType.SOUND_LIGHT_ALARM.getKeyCode(), duration.toString());
        CmdData cmdData = new CmdData();
        cmdData.setCmd("soundLightAlarm");
        cmdData.setData(new CmdSoundLightAlarm(duration));
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 智能网关重启
     * @param iotData 智能网关设备编号信息
     * @return
     */
    public AjaxResult publishReboot(IotData iotData){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.REBOOT.getKeyCode(), "");
        CmdData cmdData = new CmdData();
        cmdData.setCmd("soundLightAlarm");
        cmdData.setData(new CmdEmptyData());
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 查看智能网关状态
     * @param iotData 智能网关设备编号信息
     * @return
     */
    public AjaxResult publishGetStatus(IotData iotData){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.GET_STATUS.getKeyCode(), "");
        CmdData cmdData = new CmdData();
        cmdData.setCmd("getStatus");
        cmdData.setData(new CmdEmptyData());
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    /**
     * 远程抓取图片(机器视觉测量仪)
     * @param iotData 智能网关设备编号信息
     * @param devNo 传感器编码
     * @return
     */
    public AjaxResult publishGetImage(IotData iotData, String devNo){
        String deviceId = iotData.getDeviceId();
        // 前缀 + 设备号
        String topic = TopicConstant.TOPIC_SEND_CMD.replace("${deviceIdentity}", deviceId).replace("${cmd}", iotData.getCmd());

        Integer msgId = saveCmdMessage(CommandMessageType.GET_IMG.getKeyCode(), "");
        CmdData cmdData = new CmdData();
        cmdData.setCmd("getImg");
        cmdData.setData(new CmdGetImageDev(devNo));
        cmdData.setMsgId(msgId);
        String payload = JSONUtil.toJsonStr(cmdData);
        sendCommand(topic, payload);
        return AjaxResult.success("发送成功");
    }

    private Integer saveCmdMessage(String cmdType, String mainMessage){
        AqyMqttCmdMessage mqttCmdMessage = new AqyMqttCmdMessage();
        mqttCmdMessage.setCmdType(cmdType);
        mqttCmdMessage.setMsgId(0);//UniqueIntGenerator.generateUniqueInt()
        mqttCmdMessage.setMsgData(mainMessage);
        mqttCmdMessage.setCreateTime(DateUtils.getNowDate());
//        aqyMqttCmdMessageService.insertAqyMqttCmdMessage(mqttCmdMessage);
        return mqttCmdMessage.getMsgId();
    }

    private void sendCommand(String topic, String payload) {
        mqttSender.sendToMqtt(topic, 2, payload);
        log.info("MQTT command sent. topic={}, payload={}", topic, payload);
    }
}
