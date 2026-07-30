package com.ruoyi.common.core.domain.tree;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/10/15 15:43
 */
@Data
public class SectionTree {
    private int rowKey;
    private Long id;
    private String sectionName;
    private int level;
    private List<SectionEqmtTree> children;
    public SectionTree(){}

    public SectionTree(int rowKey, int level,Long id, String sectionName) {
        this.rowKey = rowKey;
        this.level = level;
        this.id = id;
        this.sectionName = sectionName;
        this.children = new ArrayList<>();
    }

    public static SectionTree getItem(Long id, List<SectionTree> list){
        if(list != null){
            for (SectionTree item : list) {
                if(item.getId().equals(id))
                    return item;
            }
        }
        return null;
    }
}
