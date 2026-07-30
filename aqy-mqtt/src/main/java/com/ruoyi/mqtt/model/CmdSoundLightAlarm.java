package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/12 11:00
 */
@Data
public class CmdSoundLightAlarm extends CmdDevBase{
    private Long duration;

    public CmdSoundLightAlarm(Long duration){
        this.duration = duration;
    }
}
