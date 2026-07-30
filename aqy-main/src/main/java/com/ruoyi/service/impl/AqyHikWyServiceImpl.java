package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyHikWy;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.mapper.AqyHikWyMapper;
import com.ruoyi.service.IAqyHikWyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 海康位移数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-03-07
 */
@Service
public class AqyHikWyServiceImpl implements IAqyHikWyService
{
    @Autowired
    private AqyHikWyMapper aqyHikWyMapper;

    /**
     * 查询海康位移数据
     * 
     * @param id 海康位移数据主键
     * @return 海康位移数据
     */
    @Override
    public AqyHikWy selectAqyHikWyById(Long id)
    {
        return aqyHikWyMapper.selectAqyHikWyById(id);
    }

    /**
     * 查询海康位移数据列表
     * 
     * @param aqyHikWy 海康位移数据
     * @return 海康位移数据
     */
    @Override
    public List<AqyHikWy> selectAqyHikWyList(AqyHikWy aqyHikWy)
    {
        return aqyHikWyMapper.selectAqyHikWyList(aqyHikWy);
    }

    /**
     * 新增海康位移数据
     * 
     * @param aqyHikWy 海康位移数据
     * @return 结果
     */
    @Override
    public int insertAqyHikWy(AqyHikWy aqyHikWy)
    {
        aqyHikWy.setCreateTime(DateUtils.getNowDate());
        return aqyHikWyMapper.insertAqyHikWy(aqyHikWy);
    }

    /**
     * 修改海康位移数据
     * 
     * @param aqyHikWy 海康位移数据
     * @return 结果
     */
    @Override
    public int updateAqyHikWy(AqyHikWy aqyHikWy)
    {
        return aqyHikWyMapper.updateAqyHikWy(aqyHikWy);
    }

    /**
     * 批量删除海康位移数据
     * 
     * @param ids 需要删除的海康位移数据主键
     * @return 结果
     */
    @Override
    public int deleteAqyHikWyByIds(Long[] ids)
    {
        return aqyHikWyMapper.deleteAqyHikWyByIds(ids);
    }

    /**
     * 删除海康位移数据信息
     * 
     * @param id 海康位移数据主键
     * @return 结果
     */
    @Override
    public int deleteAqyHikWyById(Long id)
    {
        return aqyHikWyMapper.deleteAqyHikWyById(id);
    }
}
