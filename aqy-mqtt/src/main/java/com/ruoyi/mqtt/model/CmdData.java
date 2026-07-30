package com.ruoyi.mqtt.model;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/11 17:09
 */
@Data
public class CmdData {
    private String cmd;

    private CmdDevBase data;

    private Integer msgId;
}
