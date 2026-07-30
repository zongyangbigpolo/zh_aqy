package com.ruoyi.common.core.domain.aqy;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 靶标数据对象 aqy_equipment_target_data
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
public class AqyEquipmentTargetData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 靶标ID */
    @Excel(name = "靶标ID")
    private Long targetId;

    /** 测点ID */
    @Excel(name = "测点ID")
    private Long measitemId;

    /** 数值 */
    @Excel(name = "数值")
    private BigDecimal value;

    /** 上传时间戳 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String catchTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTargetId(Long targetId) 
    {
        this.targetId = targetId;
    }

    public Long getTargetId() 
    {
        return targetId;
    }
    public void setMeasitemId(Long measitemId) 
    {
        this.measitemId = measitemId;
    }

    public Long getMeasitemId() 
    {
        return measitemId;
    }
    public void setValue(BigDecimal value) 
    {
        this.value = value;
    }

    public BigDecimal getValue() 
    {
        return value;
    }

    public String getCatchTime() {
        return catchTime;
    }

    public void setCatchTime(String catchTime) {
        this.catchTime = catchTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("targetId", getTargetId())
            .append("measitemId", getMeasitemId())
            .append("value", getValue())
            .append("catchTime", getCatchTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
