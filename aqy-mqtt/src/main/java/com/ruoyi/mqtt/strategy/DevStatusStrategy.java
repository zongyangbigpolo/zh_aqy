package com.ruoyi.mqtt.strategy;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.websocket.WebSocketServer;
import com.ruoyi.common.websocket.WebSocketUsers;
import com.ruoyi.service.IAqyEquipmentService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 智能网关上传状态
 * @Author：MXJ
 * @Date：2024/10/11 16:41
 */
@Slf4j
@Component
public class DevStatusStrategy implements MessageHandlerStrategy{
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;

    @Override
    public void handle(String payload) {
        log.info("DevStatusStrategy收到消息:" + payload);
        List<DevStatus> beans = JSON.parseArray(payload, DevStatus.class);
        if(beans != null &&  beans.size() > 0) {
            for (DevStatus devStatus : beans) {
                List<AqyEquipment> aqyEquipmentList = aqyEquipmentService.selectAqyEquipmentByCode(devStatus.getDevNo());
                if(aqyEquipmentList != null){
                    for (AqyEquipment aqyEquipment : aqyEquipmentList) {
                        if(devStatus.getCode() != null) {
                            aqyEquipment.setOnlineStatus(devStatus.getCode());
                        }else{
                            aqyEquipment.setOnlineStatus(null);
                        }
                        aqyEquipmentService.updateAqyEquipment(aqyEquipment);
                    }
                }
            }
            // 通过WebSockets通知前端更新设备状态
            WebSocketUsers.pushMessage(1, -1, null);
        }
    }

    @Override
    public String getTopicPattern() {
        return "\\$devstatus";
    }

    private String topic;

    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }
}

@Data
class DevStatus{
    private String devNo;

    private Double CPUTem;

    private Double GPUTem;

    //0，未充电；1，充电
    private Integer batMode;

    private Double battery;

    private Double camRotationAngle;

    //0，通讯正常；1，通讯异常
    private Integer code;

    // 当前版本号
    private String curVersion;

    private Double rotationAngle;

    private Double tem;

    private Double tem2;

    private Double tem3;

    private Integer tempError;

    //设备工作模式，0-正常模式，1-加急模式
    private Integer workMode;
//
//
//
//    private Double uplinkRssi;
//
//    private Double downlinkRssi;
//
//    // 电压上下限，逗号前面的代表电压下限
//    private String batVolt;
//
//    // 0:存储器正常，1：存储器异常
//    private Integer memStatus;

}
