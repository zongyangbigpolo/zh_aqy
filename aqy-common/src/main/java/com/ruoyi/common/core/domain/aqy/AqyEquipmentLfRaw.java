package com.ruoyi.common.core.domain.aqy;

import java.math.BigDecimal;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 裂缝监测设备上传数据记录对象 aqy_equipment_lf_raw
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyEquipmentLfRaw extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 设备ID
     */
    @Excel(name = "设备ID")
    private Long eqmtId;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String eqmtCode;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String eqmtName;

    /**
     * 上传时间戳
     */
    @Excel(name = "上传时间戳")
    private Long catchTime;

    /**
     * 裂缝(mm)
     */
    @Excel(name = "裂缝(mm)")
    private BigDecimal lfValue;

    /**
     * 温度(℃)
     */
    @Excel(name = "温度(℃)")
    private BigDecimal tempValue;

    private String eqmtTypeName;

    private String unitName;

    private Integer limit;

    private Long projectId;

    private String eqmtTypeSymbol;

    private BigDecimal initialX;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("eqmtId", getEqmtId())
                .append("eqmtCode", getEqmtCode())
                .append("eqmtName", getEqmtName())
                .append("catchTime", getCatchTime())
                .append("lfValue", getLfValue())
                .append("tempValue", getTempValue())
                .toString();
    }
}
