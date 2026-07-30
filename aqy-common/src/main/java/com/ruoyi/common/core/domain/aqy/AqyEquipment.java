package com.ruoyi.common.core.domain.aqy;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 数据采集设备对象 aqy_equipment
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Data
public class AqyEquipment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;
    private int rowKey;
    /** 工程项目 */
    @Excel(name = "工程项目")
    private Long projectId;

    /** 所属智能网关 */
    @Excel(name = "所属智能网关")
    private Long gatwayId;

    /** 设备类型ID */
    @Excel(name = "设备类型ID")
    private Long eqmtTypeId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String eqmtName;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String eqmtCode;

    /** 靶标序号 */
    @Excel(name = "靶标序号")
    private Integer sortNum;

    /** 经度 */
    @Excel(name = "经度")
    private BigDecimal longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private BigDecimal latitude;

    /** 高程 */
    @Excel(name = "高程")
    private BigDecimal elevation;

    /** 方位角 */
    @Excel(name = "方位角")
    private BigDecimal azimuthAngle;

    /** 初始坐标X */
    @Excel(name = "初始坐标X")
    private BigDecimal initialX;

    /** 初始坐标Y */
    @Excel(name = "初始坐标Y")
    private BigDecimal initialY;

    /** 初始坐标H */
    @Excel(name = "初始坐标H")
    private BigDecimal initialH;

    /** 累计变化值 */
    @Excel(name = "累计变化值")
    private BigDecimal accumulativeChangeValue;

    /** 累计变化值 */
    @Excel(name = "累计变化值")
    private BigDecimal accumulativeChangeValueX;

    /** 累计变化值 */
    @Excel(name = "累计变化值")
    private BigDecimal accumulativeChangeValueY;

    /** 累计变化值 */
    @Excel(name = "累计变化值")
    private BigDecimal accumulativeChangeValueH;

    /** 瞬时变化值 */
    @Excel(name = "瞬时变化值")
    private BigDecimal instantChangeValue;

    /** 单位 */
    @Excel(name = "单位")
    private String unitName;

    /** 当前报警等级 */
    @Excel(name = "当前报警等级")
    private Long alarmLevel;

    /** 在线状态 */
    @Excel(name = "在线状态")
    private Integer onlineStatus;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Integer isDelete;

    // 所属机器视觉测量仪名称
    private String visualEqmtName;

    // 所属机器视觉测量仪编码
    private String visualEqmtCode;

    private String xOrY;

    private Long qrtzJobId;

    private boolean shouldCreateQrtzJob;

    /**
     * 是否可以抓取照片
     */
    private Boolean canCatchImage;

    private AqyEquipmentType eqmtTypeData;
    private List<AqyEquipmentFile> eqmtFileList;
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("projectId", getProjectId())
            .append("gatwayId", getGatwayId())
            .append("eqmtTypeId", getEqmtTypeId())
            .append("eqmtName", getEqmtName())
            .append("eqmtCode", getEqmtCode())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .append("elevation", getElevation())
            .append("azimuthAngle", getAzimuthAngle())
            .append("initialX", getInitialX())
            .append("initialY", getInitialY())
            .append("initialH", getInitialH())
            .append("instantChangeValue", getInstantChangeValue())
            .append("alarmLevel", getAlarmLevel())
            .append("onlineStatus", getOnlineStatus())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("createUid", getCreateUid())
            .append("isDelete", getIsDelete())
            .toString();
    }
}
