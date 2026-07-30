package com.ruoyi.common.core.domain.aqy;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 记录发送到智能网关的信息对象 aqy_mqtt_cmd_message
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyMqttCmdMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 指令类型 */
    @Excel(name = "指令类型")
    private String cmdType;

    /** MsgId */
    @Excel(name = "MsgId")
    private Integer msgId;

    /** 发送内容 */
    @Excel(name = "发送内容")
    private String msgData;

    /** 回复内容 */
    @Excel(name = "回复内容")
    private String replyRest;

    private String eqmtCode;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("cmdType", getCmdType())
            .append("msgId", getMsgId())
            .append("msgData", getMsgData())
            .append("replyRest", getReplyRest())
            .append("createTime", getCreateTime())
            .toString();
    }
}
