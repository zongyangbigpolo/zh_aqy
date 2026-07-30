package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/11 16:16
 */
@Data
public class CmdDownConfigDev extends CmdDevBase{
    private String[] devList;

    public CmdDownConfigDev(String[] devList){
        this.devList = devList;
    }
}
