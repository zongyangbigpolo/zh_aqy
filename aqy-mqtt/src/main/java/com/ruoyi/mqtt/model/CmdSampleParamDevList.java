package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/11 16:48
 */
@Data
public class CmdSampleParamDevList extends CmdDevBase{
    private CmdSampleParamDev[] data;

    public CmdSampleParamDevList(CmdSampleParamDev[] data){
        this.data = data;
    }
}
