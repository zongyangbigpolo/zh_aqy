package com.ruoyi.common.core.domain.aqy;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 采集设备的证书文件对象 aqy_equipment_file
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyEquipmentFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 工程项目ID */
    @Excel(name = "工程项目ID")
    private Long projectId;

    /** 采集设备ID */
    @Excel(name = "采集设备ID")
    private Long eqmtId;
    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String fileUrl;

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
    public void setEqmtId(Long eqmtId)
    {
        this.eqmtId = eqmtId;
    }

    public Long getEqmtId()
    {
        return eqmtId;
    }
    public void setFileUrl(String fileUrl)
    {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl()
    {
        return fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("projectId", getProjectId())
            .append("eqmtId", getEqmtId())
            .append("fileUrl", getFileUrl())
            .append("createTime", getCreateTime())
            .toString();
    }
}
