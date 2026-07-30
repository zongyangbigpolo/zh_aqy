package com.ruoyi.common.core.domain.aqy;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 声光报警设备对象 aqy_alarm_equipment
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyAlarmEquipment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 工程项目 */
    @Excel(name = "工程项目")
    private Long projectId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String eqmtName;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String eqmtCode;

    /** 在线状态 */
    @Excel(name = "在线状态")
    private Integer onlineStatus;

    @Excel(name = "报警时间")
    private Long alarmTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer isDelete;

    /** 经度 */
    @Excel(name = "经度")
    private BigDecimal longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private BigDecimal latitude;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("projectId", getProjectId())
            .append("eqmtName", getEqmtName())
            .append("eqmtCode", getEqmtCode())
            .append("onlineStatus", getOnlineStatus())
            .append("createTime", getCreateTime())
            .append("createUid", getCreateUid())
            .append("isDelete", getIsDelete())
            .toString();
    }
}
