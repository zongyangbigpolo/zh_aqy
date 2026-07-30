package com.ruoyi.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.core.domain.aqy.AqySection;
import com.ruoyi.common.core.domain.aqy.AqySectionEqmt;
import com.ruoyi.common.core.domain.tree.EquipmentTree;
import com.ruoyi.common.core.domain.tree.SectionEqmtTree;
import com.ruoyi.common.core.domain.tree.SectionTree;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.mapper.AqySectionEqmtMapper;
import com.ruoyi.mapper.AqySectionMapper;
import com.ruoyi.service.IAqySectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 断面信息Service业务层处理
 *
 * @author MXJ
 * @date 2024-09-11
 */
@Service
public class AqySectionServiceImpl implements IAqySectionService {
    @Autowired
    private AqySectionMapper aqySectionMapper;
    @Autowired
    private AqySectionEqmtMapper aqySectionEqmtMapper;

    /**
     * 查询断面信息
     *
     * @param id 断面信息主键
     * @return 断面信息
     */
    @Override
    public AqySection selectAqySectionById(Long id) {
        ArrayList<Long> eqmtTypeIds = new ArrayList<Long>();
        AqySection aqySection = aqySectionMapper.selectAqySectionById(id);
        if (aqySection.getChildren() != null && aqySection.getChildren().size() > 0) {
            for (AqySectionEqmt aqySectionEqmt : aqySection.getChildren()) {
                eqmtTypeIds.add(aqySectionEqmt.getEqmtTypeId());
            }
        }
        aqySection.setEqmtTypeIds(eqmtTypeIds);
        return aqySection;
    }

    /**
     * 查询断面信息列表
     *
     * @param aqySection 断面信息
     * @return 断面信息
     */
    @Override
    public List<AqySection> selectAqySectionList(AqySection aqySection) {
        return aqySectionMapper.selectAqySectionList(aqySection);
    }

    @Override
    public List<SectionTree> selectAqySectionListTree(AqySection aqySection) {
        List<AqySection> list = aqySectionMapper.selectAqySectionList(aqySection);
        List<SectionTree> sectionTreeList = null;
        List<SectionEqmtTree> sectionEqmtTreeList;
        EquipmentTree equipmentTree;
        if(list!= null && list.size() > 0){
            sectionTreeList = new ArrayList<>();
            int rowKey = 1;
            for (AqySection section : list) {
                SectionTree sectionTree=SectionTree.getItem(section.getId(),sectionTreeList);
                if (sectionTree == null) {
                    sectionTreeList.add(sectionTree = new SectionTree(rowKey++, 1,section.getId(),section.getSectionName() ));
                }
                 sectionEqmtTreeList = sectionTree.getChildren();
                for (AqySectionEqmt child : section.getChildren()) {
                    SectionEqmtTree sectionEqmtTree=SectionEqmtTree.getItem(child.getId(),sectionEqmtTreeList);
                    if (sectionEqmtTree == null) {
                        sectionEqmtTreeList.add(sectionEqmtTree = new SectionEqmtTree(rowKey++, 2, child.getId(),child.getEqmtTypeId()));
                    }

                    for (AqyEquipment childChild : child.getChildren()) {
                        sectionEqmtTree.getChildren().add(equipmentTree = new EquipmentTree(rowKey++, 3, childChild.getId(),childChild.getEqmtTypeId(),childChild.getEqmtName()));

                    }
                }

            }
        }
        return sectionTreeList;
    }

    /**
     * 新增断面信息
     *
     * @param aqySection 断面信息
     * @return 结果
     */
    @Override
    public int insertAqySection(AqySection aqySection) {
        aqySection.setCreateTime(DateUtils.getNowDate());
        int result = aqySectionMapper.insertAqySection(aqySection);
        if (result > 0) {
            AqySectionEqmt aqySectionEqmt = new AqySectionEqmt();
            aqySectionEqmt.setCreateTime(DateUtils.getNowDate());
            aqySectionEqmt.setSectionId(aqySection.getId());
            for (int i = 0; i < aqySection.getEqmtTypeIds().size(); i++) {
                aqySectionEqmt.setEqmtTypeId(aqySection.getEqmtTypeIds().get(i));
                aqySectionEqmtMapper.insertAqySectionEqmt(aqySectionEqmt);
            }
        }
        return result;
    }

    /**
     * 修改断面信息
     *
     * @param aqySection 断面信息
     * @return 结果
     */
    @Override
    public int updateAqySection(AqySection aqySection) {
        int result = aqySectionMapper.updateAqySection(aqySection);
        if (result > 0) {
            //删除原有关联的设备信息
            aqySectionEqmtMapper.deleteAqySectionEqmtBySectionId(aqySection.getId());
            //添加新的关联的设备信息
            AqySectionEqmt aqySectionEqmt = new AqySectionEqmt();
            aqySectionEqmt.setCreateTime(DateUtils.getNowDate());
            aqySectionEqmt.setSectionId(aqySection.getId());
            for (int i = 0; i < aqySection.getEqmtTypeIds().size(); i++) {
                aqySectionEqmt.setEqmtTypeId(aqySection.getEqmtTypeIds().get(i));
                aqySectionEqmtMapper.insertAqySectionEqmt(aqySectionEqmt);
            }
        }
        return  result;
    }

    /**
     * 批量删除断面信息
     *
     * @param ids 需要删除的断面信息主键
     * @return 结果
     */
    @Override
    public int deleteAqySectionByIds(Long[] ids) {
        int result= aqySectionMapper.deleteAqySectionByIds(ids);
        for (int i = 0; i < ids.length; i++) {
            aqySectionEqmtMapper.deleteAqySectionEqmtBySectionId(ids[i]);
        }
        return result;
    }

    /**
     * 删除断面信息信息
     *
     * @param id 断面信息主键
     * @return 结果
     */
    @Override
    public int deleteAqySectionById(Long id) {
        int result= aqySectionMapper.deleteAqySectionById(id);
        aqySectionEqmtMapper.deleteAqySectionEqmtBySectionId(id);
        return result;
    }


}
