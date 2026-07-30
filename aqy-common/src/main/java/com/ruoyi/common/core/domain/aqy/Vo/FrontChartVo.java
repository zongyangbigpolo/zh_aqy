package com.ruoyi.common.core.domain.aqy.Vo;

import com.ruoyi.common.utils.DateUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FrontChartVo {
    private Long eqmtId;

    private Integer sortNum;

    private String name;

    private Long catchTime;

    private String xOrY;

    // 累计变化值
    private BigDecimal valueWy;

    private BigDecimal valueLf;

    private BigDecimal valueYl;

    private BigDecimal valueQjX;

    private BigDecimal valueQjY;

    private BigDecimal valueQjZ;

    private String catchTimeMark;
    public String getCatchTimeMark(){
        if(this.catchTime != null)
            return this.catchTimeMark = DateUtils.parseDateToStr("M/d HH:mm", new Date(this.catchTime));
        else
            return this.catchTimeMark = null;
    }

}
