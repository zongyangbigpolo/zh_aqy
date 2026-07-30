package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyMqttCmdMessage;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyMqttCmdMessageMapper;
import com.ruoyi.service.IAqyMqttCmdMessageService;

/**
 * 记录发送到智能网关的信息Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyMqttCmdMessageServiceImpl implements IAqyMqttCmdMessageService
{
    @Autowired
    private AqyMqttCmdMessageMapper aqyMqttCmdMessageMapper;

    /**
     * 查询记录发送到智能网关的信息
     *
     * @param id 记录发送到智能网关的信息主键
     * @return 记录发送到智能网关的信息
     */
    @Override
    public AqyMqttCmdMessage selectAqyMqttCmdMessageById(Long id)
    {
        return aqyMqttCmdMessageMapper.selectAqyMqttCmdMessageById(id);
    }

    /**
     * 查询记录发送到智能网关的信息列表
     *
     * @param aqyMqttCmdMessage 记录发送到智能网关的信息
     * @return 记录发送到智能网关的信息
     */
    @Override
    public List<AqyMqttCmdMessage> selectAqyMqttCmdMessageList(AqyMqttCmdMessage aqyMqttCmdMessage)
    {
        return aqyMqttCmdMessageMapper.selectAqyMqttCmdMessageList(aqyMqttCmdMessage);
    }

    /**
     * 新增记录发送到智能网关的信息
     *
     * @param aqyMqttCmdMessage 记录发送到智能网关的信息
     * @return 结果
     */
    @Override
    public int insertAqyMqttCmdMessage(AqyMqttCmdMessage aqyMqttCmdMessage)
    {
        aqyMqttCmdMessage.setCreateTime(DateUtils.getNowDate());
        return aqyMqttCmdMessageMapper.insertAqyMqttCmdMessage(aqyMqttCmdMessage);
    }

    /**
     * 修改记录发送到智能网关的信息
     *
     * @param aqyMqttCmdMessage 记录发送到智能网关的信息
     * @return 结果
     */
    @Override
    public int updateAqyMqttCmdMessage(AqyMqttCmdMessage aqyMqttCmdMessage)
    {
        return aqyMqttCmdMessageMapper.updateAqyMqttCmdMessage(aqyMqttCmdMessage);
    }

    /**
     * 批量删除记录发送到智能网关的信息
     *
     * @param ids 需要删除的记录发送到智能网关的信息主键
     * @return 结果
     */
    @Override
    public int deleteAqyMqttCmdMessageByIds(Long[] ids)
    {
        return aqyMqttCmdMessageMapper.deleteAqyMqttCmdMessageByIds(ids);
    }

    /**
     * 删除记录发送到智能网关的信息信息
     *
     * @param id 记录发送到智能网关的信息主键
     * @return 结果
     */
    @Override
    public int deleteAqyMqttCmdMessageById(Long id)
    {
        return aqyMqttCmdMessageMapper.deleteAqyMqttCmdMessageById(id);
    }

    @Override
    public AqyMqttCmdMessage selectAqyMqttCmdMessageByMsgId(Integer msgId, String cmd) {
        return aqyMqttCmdMessageMapper.selectAqyMqttCmdMessageByMsgId(msgId, cmd);
    }
}
