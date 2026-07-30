package com.ruoyi.common.core.domain.aqy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 靶标管理对象 aqy_equipment_target
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
public class AqyEquipmentTarget extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 设备id */
    @Excel(name = "设备id")
    private Long equipmentId;

    /** 测点id */
    @Excel(name = "测点id")
    private Long measitemId;

    /** 靶标名称 */
    @Excel(name = "靶标名称")
    private String targetName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setEquipmentId(Long equipmentId) 
    {
        this.equipmentId = equipmentId;
    }

    public Long getEquipmentId() 
    {
        return equipmentId;
    }
    public void setMeasitemId(Long measitemId) 
    {
        this.measitemId = measitemId;
    }

    public Long getMeasitemId() 
    {
        return measitemId;
    }
    public void setTargetName(String targetName) 
    {
        this.targetName = targetName;
    }

    public String getTargetName() 
    {
        return targetName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("equipmentId", getEquipmentId())
            .append("measitemId", getMeasitemId())
            .append("targetName", getTargetName())
            .toString();
    }
}
