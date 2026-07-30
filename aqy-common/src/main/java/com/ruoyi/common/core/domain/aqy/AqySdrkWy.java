package com.ruoyi.common.core.domain.aqy;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 山东仁科位移数据对象 aqy_sdrk_wy
 * 
 * @author ruoyi
 * @date 2025-02-21
 */
public class AqySdrkWy extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 设备地址编号 */
    @Excel(name = "设备地址编号")
    private String deviceAddr;

    /** 节点id */
    @Excel(name = "节点id")
    private Long nodeId;

    /** 水平位移（m） */
    @Excel(name = "水平位移", readConverterExp = "m=")
    private BigDecimal valueWyX;

    /** 垂直位移（m） */
    @Excel(name = "垂直位移", readConverterExp = "m=")
    private BigDecimal valueWyY;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String catchTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUid;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDeviceAddr(String deviceAddr) 
    {
        this.deviceAddr = deviceAddr;
    }

    public String getDeviceAddr() 
    {
        return deviceAddr;
    }
    public void setNodeId(Long nodeId) 
    {
        this.nodeId = nodeId;
    }

    public Long getNodeId() 
    {
        return nodeId;
    }

    public BigDecimal getValueWyX() {
        return valueWyX;
    }

    public void setValueWyX(BigDecimal valueWyX) {
        this.valueWyX = valueWyX;
    }

    public BigDecimal getValueWyY() {
        return valueWyY;
    }

    public void setValueWyY(BigDecimal valueWyY) {
        this.valueWyY = valueWyY;
    }

    public String getCatchTime() {
        return catchTime;
    }

    public void setCatchTime(String catchTime) {
        this.catchTime = catchTime;
    }

    public void setCreateUid(Long createUid)
    {
        this.createUid = createUid;
    }

    public Long getCreateUid() 
    {
        return createUid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceAddr", getDeviceAddr())
            .append("nodeId", getNodeId())
            .append("valueWyX", getValueWyX())
            .append("valueWyY", getValueWyY())
            .append("catchTime", getCatchTime())
            .append("createTime", getCreateTime())
            .append("createUid", getCreateUid())
            .toString();
    }
}
