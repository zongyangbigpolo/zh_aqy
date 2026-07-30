package com.ruoyi.mapper;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqySection;


/**
 * 断面信息Mapper接口
 *
 * @author MXJ
 * @date 2024-09-11
 */
public interface AqySectionMapper
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
     * 删除断面信息
     *
     * @param id 断面信息主键
     * @return 结果
     */
    public int deleteAqySectionById(Long id);

    /**
     * 批量删除断面信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqySectionByIds(Long[] ids);
}
