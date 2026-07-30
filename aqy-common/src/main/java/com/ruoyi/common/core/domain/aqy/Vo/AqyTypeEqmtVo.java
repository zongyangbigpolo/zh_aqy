package com.ruoyi.common.core.domain.aqy.Vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 大屏上面每种类型设备的总量、在线数量、累计位移量
 * @Author：MXJ
 * @Date：2024/10/23 17:09
 */
@Data
public class AqyTypeEqmtVo {
    private static final long serialVersionUID = 1L;

    private Long eqmtTypeId;

    private String eqmtTypeName;

    private String eqmtTypeSymbol;

    private Integer totalCount;

    private Integer onlineCount;

    private BigDecimal accumulativeChangeValueX;

    private BigDecimal accumulativeChangeValueY;

    private BigDecimal accumulativeChangeValueZ;

    private String unitName;
}
