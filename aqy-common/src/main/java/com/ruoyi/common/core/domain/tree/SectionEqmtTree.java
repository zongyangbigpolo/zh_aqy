package com.ruoyi.common.core.domain.tree;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/10/15 15:44
 */
@Data
public class SectionEqmtTree {
    private int rowKey;
    private Long id;
    private Long sectionId;
    private Long eqmtTypeId;
    private int level;
    private List<EquipmentTree> children;
    public SectionEqmtTree(){}

    public SectionEqmtTree(int rowKey, int level,Long id, Long eqmtTypeId) {
        this.rowKey = rowKey;
        this.level = level;
        this.id = id;
        this.sectionId = sectionId;
        this.eqmtTypeId = eqmtTypeId;
        this.children = new ArrayList<>();
    }

    public static SectionEqmtTree getItem(Long id, List<SectionEqmtTree> list){
        if(list != null){
            for (SectionEqmtTree item : list) {
                if(item.getId().equals(id))
                    return item;
            }
        }
        return null;
    }
}
