package com.ruoyi.common.core.domain.aqy;

import java.math.BigDecimal;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报警等级对象 aqy_alarm
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyAlarm extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 设备类型ID
     */
    @Excel(name = "设备类型ID")
    private Long eqmtType;

    /**
     * 报警等级
     */
    @Excel(name = "报警等级")
    private Integer alarmLevel;

    /**
     * 每日变化阈值
     */
    @Excel(name = "每日变化阈值")
    private BigDecimal instantThresholdValue;

    /**
     * 累计变化阈值
     */
    @Excel(name = "累计变化阈值")
    private BigDecimal accumulativeThresholdValue;

    /**
     * 上限
     */
    @Excel(name = "上限")
    private BigDecimal upperLimit;

    /**
     * 下限
     */
    @Excel(name = "下限")
    private BigDecimal lowerLimit;

    /**
     * 报警标记颜色
     */
    @Excel(name = "报警标记颜色")
    private String alarmColor;

    /**
     * 报警内容模板
     */
    @Excel(name = "报警内容模板")
    private String alarmTemplate;

    /**
     * 信息推送次数
     */
    @Excel(name = "信息推送次数")
    private Integer alarmCount;

    private String cronExpress;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUid;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer isDelete;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("eqmtType", getEqmtType())
                .append("alarmLevel", getAlarmLevel())
                .append("instantThresholdValue", getInstantThresholdValue())
                .append("accumulativeThresholdValue", getAccumulativeThresholdValue())
                .append("alarmColor", getAlarmColor())
                .append("alarmTemplate", getAlarmTemplate())
                .append("alarmCount", getAlarmCount())
                .append("createTime", getCreateTime())
                .append("createUid", getCreateUid())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
