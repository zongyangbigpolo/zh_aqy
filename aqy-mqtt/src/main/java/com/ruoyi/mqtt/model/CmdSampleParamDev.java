package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/11 16:48
 */
@Data
public class CmdSampleParamDev{
    private String devNo;

    private Integer uploadIntv;

    private Integer plusIntv;

    private Integer samplingFrequency;
}
