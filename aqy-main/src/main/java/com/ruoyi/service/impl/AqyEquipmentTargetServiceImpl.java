package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentTarget;
import com.ruoyi.mapper.AqyEquipmentTargetMapper;
import com.ruoyi.service.IAqyEquipmentTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 靶标管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
@Service
public class AqyEquipmentTargetServiceImpl implements IAqyEquipmentTargetService
{
    @Autowired
    private AqyEquipmentTargetMapper aqyEquipmentTargetMapper;

    /**
     * 查询靶标管理
     * 
     * @param id 靶标管理主键
     * @return 靶标管理
     */
    @Override
    public AqyEquipmentTarget selectAqyEquipmentTargetById(Long id)
    {
        return aqyEquipmentTargetMapper.selectAqyEquipmentTargetById(id);
    }

    /**
     * 查询靶标管理列表
     * 
     * @param aqyEquipmentTarget 靶标管理
     * @return 靶标管理
     */
    @Override
    public List<AqyEquipmentTarget> selectAqyEquipmentTargetList(AqyEquipmentTarget aqyEquipmentTarget)
    {
        return aqyEquipmentTargetMapper.selectAqyEquipmentTargetList(aqyEquipmentTarget);
    }

    /**
     * 新增靶标管理
     * 
     * @param aqyEquipmentTarget 靶标管理
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentTarget(AqyEquipmentTarget aqyEquipmentTarget)
    {
        return aqyEquipmentTargetMapper.insertAqyEquipmentTarget(aqyEquipmentTarget);
    }

    /**
     * 修改靶标管理
     * 
     * @param aqyEquipmentTarget 靶标管理
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentTarget(AqyEquipmentTarget aqyEquipmentTarget)
    {
        return aqyEquipmentTargetMapper.updateAqyEquipmentTarget(aqyEquipmentTarget);
    }

    /**
     * 批量删除靶标管理
     * 
     * @param ids 需要删除的靶标管理主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentTargetByIds(Long[] ids)
    {
        return aqyEquipmentTargetMapper.deleteAqyEquipmentTargetByIds(ids);
    }

    /**
     * 删除靶标管理信息
     * 
     * @param id 靶标管理主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentTargetById(Long id)
    {
        return aqyEquipmentTargetMapper.deleteAqyEquipmentTargetById(id);
    }
}
