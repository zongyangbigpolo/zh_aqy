package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqyMqttCmdMessage;

import java.util.List;

/**
 * 记录发送到智能网关的信息Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyMqttCmdMessageService
{
    /**
     * 查询记录发送到智能网关的信息
     *
     * @param id 记录发送到智能网关的信息主键
     * @return 记录发送到智能网关的信息
     */
    public AqyMqttCmdMessage selectAqyMqttCmdMessageById(Long id);

    /**
     * 查询记录发送到智能网关的信息列表
     *
     * @param aqyMqttCmdMessage 记录发送到智能网关的信息
     * @return 记录发送到智能网关的信息集合
     */
    public List<AqyMqttCmdMessage> selectAqyMqttCmdMessageList(AqyMqttCmdMessage aqyMqttCmdMessage);

    /**
     * 新增记录发送到智能网关的信息
     *
     * @param aqyMqttCmdMessage 记录发送到智能网关的信息
     * @return 结果
     */
    public int insertAqyMqttCmdMessage(AqyMqttCmdMessage aqyMqttCmdMessage);

    /**
     * 修改记录发送到智能网关的信息
     *
     * @param aqyMqttCmdMessage 记录发送到智能网关的信息
     * @return 结果
     */
    public int updateAqyMqttCmdMessage(AqyMqttCmdMessage aqyMqttCmdMessage);

    /**
     * 批量删除记录发送到智能网关的信息
     *
     * @param ids 需要删除的记录发送到智能网关的信息主键集合
     * @return 结果
     */
    public int deleteAqyMqttCmdMessageByIds(Long[] ids);

    /**
     * 删除记录发送到智能网关的信息信息
     *
     * @param id 记录发送到智能网关的信息主键
     * @return 结果
     */
    public int deleteAqyMqttCmdMessageById(Long id);

    AqyMqttCmdMessage selectAqyMqttCmdMessageByMsgId(Integer msgId, String cmd);
}
