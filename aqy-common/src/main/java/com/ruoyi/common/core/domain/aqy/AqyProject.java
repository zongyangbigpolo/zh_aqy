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
 * 工程项目对象 aqy_project
 *
 * @author MXJ
 * @date 2024-09-11
 */
@Data
public class AqyProject extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 项目名称
     */
    @Excel(name = "项目名称")
    private String name;

    private String city;

    /**
     * 项目类型（数据字典）
     */
    @Excel(name = "项目类型", readConverterExp = "数=据字典")
    private String projectType;

    /**
     * 所属企业
     */
    @Excel(name = "所属企业")
    private String companyName;

    /**
     * 项目简介
     */
    @Excel(name = "项目简介")
    private String projectDesc;
    /**
     * 经度
     */
    @Excel(name = "经度")
    private BigDecimal longitude;

    /**
     * 纬度
     */
    @Excel(name = "纬度")
    private BigDecimal latitude;

    /**
     * 高程
     */
    @Excel(name = "高程")
    private BigDecimal elevation;

    /**
     * 航向角
     */
    @Excel(name = "航向角")
    private BigDecimal courseAngle;

    /**
     * 俯视角
     */
    @Excel(name = "俯视角")
    private BigDecimal depressionAngle;

    private String yzCompany;

    private String jsCompany;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date projectStartDate;

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
                .append("name", getName())
                .append("projectType", getProjectType())
                .append("companyName", getCompanyName())
                .append("longitude", getLongitude())
                .append("latitude", getLatitude())
                .append("elevation", getElevation())
                .append("courseAngle", getCourseAngle())
                .append("depressionAngle", getDepressionAngle())
                .append("createTime", getCreateTime())
                .append("createUid", getCreateUid())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
