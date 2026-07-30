package com.ruoyi.common.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程节点
 *
 * @author wyf
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInfo {

    /**
     * 数据类型 0 单号、1 数据信息
     */
    private Integer type;

    /**
     * 当前流程节点
     */
    private Integer node;

    /**
     * 推送信息
     */
    private String message;
}
