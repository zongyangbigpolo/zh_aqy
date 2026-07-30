package com.ruoyi.mapper;

import com.ruoyi.common.core.domain.aqy.AqyMqttCmdMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 记录发送到智能网关的信息Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyMqttCmdMessageMapper
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
     * 删除记录发送到智能网关的信息
     *
     * @param id 记录发送到智能网关的信息主键
     * @return 结果
     */
    public int deleteAqyMqttCmdMessageById(Long id);

    /**
     * 批量删除记录发送到智能网关的信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyMqttCmdMessageByIds(Long[] ids);

    AqyMqttCmdMessage selectAqyMqttCmdMessageByMsgId(@Param("msgId") Integer msgId, @Param("cmd") String cmd);
}
