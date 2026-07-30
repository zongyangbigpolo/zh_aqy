package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqySdrkWy;

import java.util.List;


/**
 * 山东仁科位移数据Service接口
 * 
 * @author ruoyi
 * @date 2025-02-21
 */
public interface IAqySdrkWyService 
{
    /**
     * 查询山东仁科位移数据
     * 
     * @param id 山东仁科位移数据主键
     * @return 山东仁科位移数据
     */
    public AqySdrkWy selectAqySdrkWyById(Long id);

    /**
     * 查询山东仁科位移数据列表
     * 
     * @param aqySdrkWy 山东仁科位移数据
     * @return 山东仁科位移数据集合
     */
    public List<AqySdrkWy> selectAqySdrkWyList(AqySdrkWy aqySdrkWy);

    /**
     * 新增山东仁科位移数据
     * 
     * @param aqySdrkWy 山东仁科位移数据
     * @return 结果
     */
    public int insertAqySdrkWy(AqySdrkWy aqySdrkWy);

    /**
     * 修改山东仁科位移数据
     * 
     * @param aqySdrkWy 山东仁科位移数据
     * @return 结果
     */
    public int updateAqySdrkWy(AqySdrkWy aqySdrkWy);

    /**
     * 批量删除山东仁科位移数据
     * 
     * @param ids 需要删除的山东仁科位移数据主键集合
     * @return 结果
     */
    public int deleteAqySdrkWyByIds(Long[] ids);

    /**
     * 删除山东仁科位移数据信息
     * 
     * @param id 山东仁科位移数据主键
     * @return 结果
     */
    public int deleteAqySdrkWyById(Long id);
}
