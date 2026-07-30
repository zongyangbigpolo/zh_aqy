package com.ruoyi.common.core.domain.aqy;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Author：MXJ
 * @Date：2024/10/23 13:55
 */
@Data
public class    AqyRawReport extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 设备类型ID
     */
    private Long eqmtTypeId;
    private Long eqmtId;
    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String eqmtName;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    private String eqmtCode;

    private Long startTime;

    private Long endTime;

    private Date[] timeFrame;

    private String xOrY;
    private String[]  eqmtIds;
}
