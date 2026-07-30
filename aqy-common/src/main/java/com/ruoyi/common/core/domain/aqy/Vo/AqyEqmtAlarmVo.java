package com.ruoyi.common.core.domain.aqy.Vo;

import lombok.Data;

/**
 * @Author：MXJ
 * @Date：2024/10/24 10:08
 */
@Data
public class AqyEqmtAlarmVo {
    private static final long serialVersionUID = 1L;

    private Long eqmtTypeId;

    private String eqmtTypeName;

    private Integer maxAlarmLevel;

    private Long id;

    private String eqmtName;

    private Integer onlineStatus;

    private Integer alarmLevel;
}
