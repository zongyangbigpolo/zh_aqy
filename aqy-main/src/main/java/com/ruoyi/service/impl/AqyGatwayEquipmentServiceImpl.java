package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyGatwayEquipment;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyGatwayEquipmentMapper;
import com.ruoyi.service.IAqyGatwayEquipmentService;

/**
 * 智能网关设备Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyGatwayEquipmentServiceImpl implements IAqyGatwayEquipmentService
{
    @Autowired
    private AqyGatwayEquipmentMapper aqyGatwayEquipmentMapper;

    /**
     * 查询智能网关设备
     *
     * @param id 智能网关设备主键
     * @return 智能网关设备
     */
    @Override
    public AqyGatwayEquipment selectAqyGatwayEquipmentById(Long id)
    {
        return aqyGatwayEquipmentMapper.selectAqyGatwayEquipmentById(id);
    }

    /**
     * 查询智能网关设备列表
     *
     * @param aqyGatwayEquipment 智能网关设备
     * @return 智能网关设备
     */
    @Override
    public List<AqyGatwayEquipment> selectAqyGatwayEquipmentList(AqyGatwayEquipment aqyGatwayEquipment)
    {
        return aqyGatwayEquipmentMapper.selectAqyGatwayEquipmentList(aqyGatwayEquipment);
    }

    /**
     * 新增智能网关设备
     *
     * @param aqyGatwayEquipment 智能网关设备
     * @return 结果
     */
    @Override
    public int insertAqyGatwayEquipment(AqyGatwayEquipment aqyGatwayEquipment)
    {
        aqyGatwayEquipment.setCreateTime(DateUtils.getNowDate());
        return aqyGatwayEquipmentMapper.insertAqyGatwayEquipment(aqyGatwayEquipment);
    }

    /**
     * 修改智能网关设备
     *
     * @param aqyGatwayEquipment 智能网关设备
     * @return 结果
     */
    @Override
    public int updateAqyGatwayEquipment(AqyGatwayEquipment aqyGatwayEquipment)
    {
        return aqyGatwayEquipmentMapper.updateAqyGatwayEquipment(aqyGatwayEquipment);
    }

    /**
     * 批量删除智能网关设备
     *
     * @param ids 需要删除的智能网关设备主键
     * @return 结果
     */
    @Override
    public int deleteAqyGatwayEquipmentByIds(Long[] ids)
    {
        return aqyGatwayEquipmentMapper.deleteAqyGatwayEquipmentByIds(ids);
    }

    /**
     * 删除智能网关设备信息
     *
     * @param id 智能网关设备主键
     * @return 结果
     */
    @Override
    public int deleteAqyGatwayEquipmentById(Long id)
    {
        return aqyGatwayEquipmentMapper.deleteAqyGatwayEquipmentById(id);
    }

    @Override
    public AqyGatwayEquipment selectAqyGatwayEquipmentByGatwayId(Long gatwayId) {
        return aqyGatwayEquipmentMapper.selectAqyGatwayEquipmentByGatwayId(gatwayId);
    }
}
