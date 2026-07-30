package com.ruoyi.common.core.domain.tree;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/10/15 16:15
 */
@Data
public class EquipmentTree {
    private int rowKey;
    private Long id;
    private Long eqmtTypeId;
    private String eqmtName;
    private int level;
    public EquipmentTree(){}

    public EquipmentTree(int rowKey, int level,Long id, Long eqmtTypeId, String eqmtName) {
        this.rowKey = rowKey;
        this.level = level;
        this.id = id;
        this.eqmtTypeId = eqmtTypeId;
        this.eqmtName = eqmtName;
    }


}
