package com.ruoyi.common.core.domain.aqy;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监测设备抓取照片记录对象 aqy_equipment_image
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyEquipmentImage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 监测设备 */
    @Excel(name = "监测设备")
    private Long equipmentId;

    /** 抓取照片 */
    @Excel(name = "抓取照片")
    private String imageUrl;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("equipmentId", getEquipmentId())
            .append("imageUrl", getImageUrl())
            .toString();
    }
}
