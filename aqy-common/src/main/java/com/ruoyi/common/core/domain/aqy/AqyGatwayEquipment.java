package com.ruoyi.common.core.domain.aqy;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.net.InetSocketAddress;

/**
 * 智能网关设备对象 aqy_gatway_equipment
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyGatwayEquipment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 工程项目
     */
    @Excel(name = "工程项目")
    private Long projectId;

    /**
     * 智能网关名称
     */
    @Excel(name = "智能网关名称")
    private String gatwayName;

    /**
     * 智能网关编码
     */
    @Excel(name = "智能网关编码")
    private String gatwayCode;

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

    /**
     * 工作模式
     */
    private Integer model;

    /**
     * 持续时间
     */
    private Integer duration;

    /** 经度 */
    @Excel(name = "经度")
    private BigDecimal longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private BigDecimal latitude;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("projectId", getProjectId())
                .append("gatwayName", getGatwayName())
                .append("gatwayCode", getGatwayCode())
                .append("createTime", getCreateTime())
                .append("createUid", getCreateUid())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
