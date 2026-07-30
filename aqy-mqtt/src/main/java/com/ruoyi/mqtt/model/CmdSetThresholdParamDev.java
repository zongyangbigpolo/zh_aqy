package com.ruoyi.mqtt.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author：MXJ
 * @Date：2024/10/11 17:06
 */
@Data
public class CmdSetThresholdParamDev extends CmdDevBase{
    private String devNo;

    /**
     * 划分几个等级，数组元素就是几个
     */
    private BigDecimal[] threshold;

    private BigDecimal[] upperLimit;

    private BigDecimal[] lowerLimit;

    public CmdSetThresholdParamDev(String devNo, BigDecimal[] threshold, BigDecimal[] upperLimit, BigDecimal[] lowerLimit){
        this.devNo = devNo;
        this.threshold = threshold;
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
    }
}
