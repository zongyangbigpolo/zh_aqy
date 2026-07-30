package com.ruoyi.common.core.domain.aqy.Vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WebSocketNodeVo {
    private Long eqmtId;

    private BigDecimal value;

    private Integer alarmLevel;

    private Long eqmtTypeId;

    private String eqmtTypeName;

    public WebSocketNodeVo(Long eqmtId, BigDecimal value){
        this.eqmtId = eqmtId;
        this.value = value;

    }
}
