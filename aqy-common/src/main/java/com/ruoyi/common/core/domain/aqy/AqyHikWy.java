package com.ruoyi.common.core.domain.aqy;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 海康位移数据对象 aqy_hik_wy
 * 
 * @author ruoyi
 * @date 2025-03-07
 */
public class AqyHikWy extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 设备地址编号 */
    @Excel(name = "设备地址编号")
    private String deviceAddr;

    /** X轴位移(mm) */
    @Excel(name = "X轴位移(mm)")
    private BigDecimal valueWyX;

    /** Y轴位移(mm) */
    @Excel(name = "Y轴位移(mm)")
    private BigDecimal valueWyY;

    /** Z轴位移(mm) */
    @Excel(name = "Z轴位移(mm)")
    private BigDecimal valueWyZ;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss ")
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

    public BigDecimal getValueWyZ() {
        return valueWyZ;
    }

    public void setValueWyZ(BigDecimal valueWyZ) {
        this.valueWyZ = valueWyZ;
    }

    public void setCatchTime(String catchTime)
    {
        this.catchTime = catchTime;
    }

    public String getCatchTime()
    {
        return catchTime;
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
            .append("valueWyX", getValueWyX())
            .append("valueWyY", getValueWyY())
            .append("valueWyZ", getValueWyZ())
            .append("catchTime", getCatchTime())
            .append("createTime", getCreateTime())
            .append("createUid", getCreateUid())
            .toString();
    }
}
