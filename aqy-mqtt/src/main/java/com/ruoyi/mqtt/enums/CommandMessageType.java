package com.ruoyi.mqtt.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：MXJ
 * @Date：2024/10/11 14:43
 */
public enum CommandMessageType {
    DOWN_CONFIG(1, "downConfig", "下载云平台配置到智能网关"),
    GET_SAMPLE_PARAM(2, "getSampleParam", "查看智能传感器采集和上传参数"),
    SET_SAMPLE_PARAM(3, "setSampleParam", "设置智能传感器采集和上传参数"),
    GET_THRESHOLD_PARAM(4, "getThresholdParam", "查看智能传感器阈值"),
    SET_THRESHOLD_PARAM(5, "setThresholdParam", "设置智能传感器阈值"),
    GET_WORK_MODE(6, "getWorkMode", "查看智能网关工作模式"),
    SET_WORK_MODE(7, "setWorkMode", "设置智能网关工作模式"),
    SAMPLE(8, "sample", "智能网关遥测"),
    GET_TIME(9, "getTime", "查看智能网关系统时间"),
    SET_TIME(10, "setTime", "设置智能网关系统时间"),
    SOUND_LIGHT_ALARM(11, "soundLightAlarm", "远程开启声光报警"),
    REBOOT(12, "reboot", "智能网关重启"),
    GET_STATUS(12, "getStatus", "查看智能网关状态"),
    GET_IMG(15, "getImg", "远程抓取图片（机器视觉测量仪）"),
    GET_CURRENT_IMG(16, "getCurrentTime", "智能网关获取当前时间")
    ;

    private static Map<Integer, CommandMessageType> map;

    public static CommandMessageType from(int data) {
        if (map == null) {
            map = new HashMap<>();
            for (CommandMessageType item : CommandMessageType.values()) {
                map.put(item.code, item);
            }
        }
        return map.get(data);
    }

    private final int code;

    private final String keyCode;

    private final String description;

    CommandMessageType(Integer code, String keyCode, String description){
        this.code = code;
        this.keyCode = keyCode;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public String getDescription() {
        return description;
    }
}
