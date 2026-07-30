package com.ruoyi.mapper;

import com.ruoyi.common.core.domain.aqy.AqyHikWy;

import java.util.List;


/**
 * 海康位移数据Mapper接口
 * 
 * @author ruoyi
 * @date 2025-03-07
 */
public interface AqyHikWyMapper 
{
    /**
     * 查询海康位移数据
     * 
     * @param id 海康位移数据主键
     * @return 海康位移数据
     */
    public AqyHikWy selectAqyHikWyById(Long id);

    /**
     * 查询海康位移数据列表
     * 
     * @param aqyHikWy 海康位移数据
     * @return 海康位移数据集合
     */
    public List<AqyHikWy> selectAqyHikWyList(AqyHikWy aqyHikWy);

    /**
     * 新增海康位移数据
     * 
     * @param aqyHikWy 海康位移数据
     * @return 结果
     */
    public int insertAqyHikWy(AqyHikWy aqyHikWy);

    /**
     * 修改海康位移数据
     * 
     * @param aqyHikWy 海康位移数据
     * @return 结果
     */
    public int updateAqyHikWy(AqyHikWy aqyHikWy);

    /**
     * 删除海康位移数据
     * 
     * @param id 海康位移数据主键
     * @return 结果
     */
    public int deleteAqyHikWyById(Long id);

    /**
     * 批量删除海康位移数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyHikWyByIds(Long[] ids);
}
