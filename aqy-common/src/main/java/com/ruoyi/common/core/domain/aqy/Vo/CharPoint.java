package com.ruoyi.common.core.domain.aqy.Vo;

import com.ruoyi.common.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CharPoint {
    private Long catchTime;

    private Long startTime;

    private Integer catchTimeInterval;
    public Integer getCatchTimeInterval(){
        return this.catchTimeInterval = (int) (catchTime - startTime) / (1000 * 60);
    }

    private String catchTimeMark;
    public String getCatchTimeMark(){
        return this.catchTimeMark = DateUtils.parseDateToStr("HH:mm", new Date(this.catchTime));
    }

    private BigDecimal value;

    public CharPoint(Long catchTime, Long startTime, BigDecimal value){
        this.catchTime = catchTime;
        this.startTime = startTime;
        this.value = value;
    }
}
