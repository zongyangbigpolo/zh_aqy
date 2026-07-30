package com.ruoyi.common.core.domain.aqy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报警联系人对象 aqy_alarm_person
 *
 * @author MXJ
 * @date 2024-10-13
 */
public class AqyAlarmPerson extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 工程项目 */
    @Excel(name = "工程项目")
    private Long projectId;

    /** 报警等级 */
    @Excel(name = "报警等级")
    private Integer alarmLevel;

    /** 联系人姓名 */
    @Excel(name = "联系人姓名")
    private String contactPerson;

    /** 联系人方式 */
    @Excel(name = "联系人方式")
    private String contactPersonNumber;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer isDelete;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getProjectId()
    {
        return projectId;
    }
    public void setAlarmLevel(Integer alarmLevel)
    {
        this.alarmLevel = alarmLevel;
    }

    public Integer getAlarmLevel()
    {
        return alarmLevel;
    }
    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }
    public void setContactPersonNumber(String contactPersonNumber)
    {
        this.contactPersonNumber = contactPersonNumber;
    }

    public String getContactPersonNumber()
    {
        return contactPersonNumber;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("projectId", getProjectId())
            .append("alarmLevel", getAlarmLevel())
            .append("contactPerson", getContactPerson())
            .append("contactPersonNumber", getContactPersonNumber())
            .append("createTime", getCreateTime())
            .append("createUid", getCreateUid())
            .append("isDelete", getIsDelete())
            .toString();
    }
}
