package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/12 10:47
 */
@Data
public class CmdSetWorkMode extends CmdDevBase{
    /**
     * mode 取值：
     * ⚫ 0 正常模式：智能网关进入正常的数据上报状态，上报周期为 3.1.2 中的 uploadIntv
     * ⚫ 1 应急模式：智能网关进入该模式后需立即上报数据并且进入数据加报状态，加报时间
     * 为 3.1.2 中的 plusIntv
     */
    private Integer mode;

    public CmdSetWorkMode(Integer mode){
        this.mode = mode;
    }
}
