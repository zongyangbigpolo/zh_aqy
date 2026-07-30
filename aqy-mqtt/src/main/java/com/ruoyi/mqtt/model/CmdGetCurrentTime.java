package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/12 10:55
 */
@Data
public class CmdGetCurrentTime extends CmdDevBase{
    private String currentTime;

    public CmdGetCurrentTime(String currentTime){
        this.currentTime = currentTime;
    }
}
