package com.ruoyi.common.core.domain.aqy;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报警记录对象 aqy_alarm_record
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyAlarmRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 工程项目 */
    @Excel(name = "工程项目")
    private Long projectId;

    /** 采集设备ID */
    @Excel(name = "采集设备ID")
    private Long qmtId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String eqmtName;

    /** 报警等级ID */
    @Excel(name = "报警等级ID")
    private Long alarmId;

    /** 报警等级 */
    @Excel(name = "报警等级")
    private Integer alarmLevel;

    /** 瞬时报警值 */
    @Excel(name = "瞬时报警值")
    private BigDecimal instantValue;

    /** 累计报警值 */
    @Excel(name = "累计报警值")
    private BigDecimal accumulativeValue1;

    /** 累计报警值 */
    @Excel(name = "累计报警值")
    private BigDecimal accumulativeValue2;

    /** 累计报警值 */
    @Excel(name = "累计报警值")
    private BigDecimal accumulativeValue3;

    /** 报警标记颜色 */
    @Excel(name = "报警标记颜色")
    private String alarmColor;

    /** 报警内容 */
    @Excel(name = "报警内容")
    private String alarmContent;

    /** 报警时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报警时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    private String remedialMeasures;

    private Date remedialTime;

    private Long remedialUid;

    private String eqmtTypeSymbol;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("projectId", getProjectId())
            .append("qmtId", getQmtId())
            .append("eqmtName", getEqmtName())
            .append("alarmId", getAlarmId())
            .append("alarmLevel", getAlarmLevel())
            .append("instantValue", getInstantValue())
            .append("accumulativeValue", getAccumulativeValue1())
            .append("alarmColor", getAlarmColor())
            .append("alarmContent", getAlarmContent())
            .append("recordTime", getRecordTime())
            .toString();
    }
}
