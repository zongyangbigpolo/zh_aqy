package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqySdrkWy;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.mapper.AqySdrkWyMapper;
import com.ruoyi.service.IAqySdrkWyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 山东仁科位移数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-02-21
 */
@Service
public class AqySdrkWyServiceImpl implements IAqySdrkWyService
{
    @Autowired
    private AqySdrkWyMapper aqySdrkWyMapper;

    /**
     * 查询山东仁科位移数据
     * 
     * @param id 山东仁科位移数据主键
     * @return 山东仁科位移数据
     */
    @Override
    public AqySdrkWy selectAqySdrkWyById(Long id)
    {
        return aqySdrkWyMapper.selectAqySdrkWyById(id);
    }

    /**
     * 查询山东仁科位移数据列表
     * 
     * @param aqySdrkWy 山东仁科位移数据
     * @return 山东仁科位移数据
     */
    @Override
    public List<AqySdrkWy> selectAqySdrkWyList(AqySdrkWy aqySdrkWy)
    {
        return aqySdrkWyMapper.selectAqySdrkWyList(aqySdrkWy);
    }

    /**
     * 新增山东仁科位移数据
     * 
     * @param aqySdrkWy 山东仁科位移数据
     * @return 结果
     */
    @Override
    public int insertAqySdrkWy(AqySdrkWy aqySdrkWy)
    {
        aqySdrkWy.setCreateTime(DateUtils.getNowDate());
        return aqySdrkWyMapper.insertAqySdrkWy(aqySdrkWy);
    }

    /**
     * 修改山东仁科位移数据
     * 
     * @param aqySdrkWy 山东仁科位移数据
     * @return 结果
     */
    @Override
    public int updateAqySdrkWy(AqySdrkWy aqySdrkWy)
    {
        return aqySdrkWyMapper.updateAqySdrkWy(aqySdrkWy);
    }

    /**
     * 批量删除山东仁科位移数据
     * 
     * @param ids 需要删除的山东仁科位移数据主键
     * @return 结果
     */
    @Override
    public int deleteAqySdrkWyByIds(Long[] ids)
    {
        return aqySdrkWyMapper.deleteAqySdrkWyByIds(ids);
    }

    /**
     * 删除山东仁科位移数据信息
     * 
     * @param id 山东仁科位移数据主键
     * @return 结果
     */
    @Override
    public int deleteAqySdrkWyById(Long id)
    {
        return aqySdrkWyMapper.deleteAqySdrkWyById(id);
    }
}
