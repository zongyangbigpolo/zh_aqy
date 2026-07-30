package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqySection;
import com.ruoyi.common.core.domain.tree.SectionEqmtTree;
import com.ruoyi.common.core.domain.tree.SectionTree;

import java.util.List;

/**
 * 断面信息Service接口
 *
 * @author MXJ
 * @date 2024-09-11
 */
public interface IAqySectionService
{
    /**
     * 查询断面信息
     *
     * @param id 断面信息主键
     * @return 断面信息
     */
    public AqySection selectAqySectionById(Long id);

    /**
     * 查询断面信息列表
     *
     * @param aqySection 断面信息
     * @return 断面信息集合
     */
    public List<AqySection> selectAqySectionList(AqySection aqySection);

    /**
     * 新增断面信息
     *
     * @param aqySection 断面信息
     * @return 结果
     */
    public int insertAqySection(AqySection aqySection);

    /**
     * 修改断面信息
     *
     * @param aqySection 断面信息
     * @return 结果
     */
    public int updateAqySection(AqySection aqySection);

    /**
     * 批量删除断面信息
     *
     * @param ids 需要删除的断面信息主键集合
     * @return 结果
     */
    public int deleteAqySectionByIds(Long[] ids);

    /**
     * 删除断面信息信息
     *
     * @param id 断面信息主键
     * @return 结果
     */
    public int deleteAqySectionById(Long id);

    List<SectionTree> selectAqySectionListTree(AqySection aqySection);
}
