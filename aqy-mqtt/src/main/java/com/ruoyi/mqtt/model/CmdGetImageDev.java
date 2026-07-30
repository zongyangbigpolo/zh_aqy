package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/12 11:11
 */
@Data
public class CmdGetImageDev extends CmdDevBase{
    private String devNo;

    public CmdGetImageDev(String devNo){
        this.devNo = devNo;
    }
}
