package com.ruoyi.common.core.domain.aqy;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 断面配置的监测设备类型对象 aqy_section_eqmt
 *
 * @author MXJ
 * @date 2024-09-11
 */
@Data
public class AqySectionEqmt extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;
    private int rowKey;
    /** 监测断面ID */
    @Excel(name = "监测断面ID")
    private Long sectionId;

    /** 设备类型ID */
    @Excel(name = "设备类型ID")
    private Long eqmtTypeId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer isDelete;

    private List<AqyEquipment> children;

    public List<AqyEquipment> getChildren() {
        return children;
    }

    public void setChildren(List<AqyEquipment> children) {
        this.children = children;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setSectionId(Long sectionId)
    {
        this.sectionId = sectionId;
    }

    public Long getSectionId()
    {
        return sectionId;
    }
    public void setEqmtTypeId(Long eqmtTypeId)
    {
        this.eqmtTypeId = eqmtTypeId;
    }

    public Long getEqmtTypeId()
    {
        return eqmtTypeId;
    }
    public void setCreateUid(Long createUid)
    {
        this.createUid = createUid;
    }

    public Long getCreateUid()
    {
        return createUid;
    }
    public void setIsDelete(Integer isDelete)
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete()
    {
        return isDelete;
    }

    public int getRowKey() {
        return rowKey;
    }

    public void setRowKey(int rowKey) {
        this.rowKey = rowKey;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sectionId", getSectionId())
            .append("eqmtTypeId", getEqmtTypeId())
            .append("createTime", getCreateTime())
            .append("createUid", getCreateUid())
            .append("isDelete", getIsDelete())
            .toString();
    }
}
