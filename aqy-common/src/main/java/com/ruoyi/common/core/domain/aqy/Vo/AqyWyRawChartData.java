package com.ruoyi.common.core.domain.aqy.Vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author：MXJ
 * @Date：2024/10/24 11:07
 */
@Data
public class AqyWyRawChartData {
    private Long eqmtId;

    private String eqmtName;

    private String unitName;

    private List<CharPoint> valuesX;

    private List<CharPoint> valuesY;

    private List<CharPoint> valuesZ;

    private List<ChartXAxisMark> xAxisMarks;

    private Long catchTime;
}

