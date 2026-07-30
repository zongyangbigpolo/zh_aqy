package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/12 10:55
 */
@Data
public class CmdSetTime extends CmdDevBase{
    private String curTime;

    public CmdSetTime(String curTime){
        this.curTime = curTime;
    }
}
