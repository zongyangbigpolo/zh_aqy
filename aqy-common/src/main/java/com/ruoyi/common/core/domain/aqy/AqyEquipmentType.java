package com.ruoyi.common.core.domain.aqy;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备类型对象 aqy_equipment_type
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyEquipmentType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;

    /**
     * 设备类型名称
     */
    @Excel(name = "设备类型名称")
    private String eqmtTypeName;

    /**
     * 设备类型标志
     */
    @Excel(name = "设备类型标志")
    private String eqmtTypeSymbol;

    /**
     * 正常上传间隔(s)
     */
    @Excel(name = "正常上传间隔(s)")
    private Integer uploadIntv;

    /**
     * 加报上传间隔(s)
     */
    @Excel(name = "加报上传间隔(s)")
    private Integer plusIntv;

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
                .append("eqmtTypeName", getEqmtTypeName())
                .append("uploadIntv", getUploadIntv())
                .append("plusIntv", getPlusIntv())
                .append("createTime", getCreateTime())
                .append("createUid", getCreateUid())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
