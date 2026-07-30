package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyAlarmEquipment;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyAlarmEquipmentMapper;
import com.ruoyi.service.IAqyAlarmEquipmentService;

/**
 * 声光报警设备Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyAlarmEquipmentServiceImpl implements IAqyAlarmEquipmentService
{
    @Autowired
    private AqyAlarmEquipmentMapper aqyAlarmEquipmentMapper;

    /**
     * 查询声光报警设备
     *
     * @param id 声光报警设备主键
     * @return 声光报警设备
     */
    @Override
    public AqyAlarmEquipment selectAqyAlarmEquipmentById(Long id)
    {
        return aqyAlarmEquipmentMapper.selectAqyAlarmEquipmentById(id);
    }

    /**
     * 查询声光报警设备列表
     *
     * @param aqyAlarmEquipment 声光报警设备
     * @return 声光报警设备
     */
    @Override
    public List<AqyAlarmEquipment> selectAqyAlarmEquipmentList(AqyAlarmEquipment aqyAlarmEquipment)
    {
        return aqyAlarmEquipmentMapper.selectAqyAlarmEquipmentList(aqyAlarmEquipment);
    }

    /**
     * 新增声光报警设备
     *
     * @param aqyAlarmEquipment 声光报警设备
     * @return 结果
     */
    @Override
    public int insertAqyAlarmEquipment(AqyAlarmEquipment aqyAlarmEquipment)
    {
        aqyAlarmEquipment.setCreateTime(DateUtils.getNowDate());
        return aqyAlarmEquipmentMapper.insertAqyAlarmEquipment(aqyAlarmEquipment);
    }

    /**
     * 修改声光报警设备
     *
     * @param aqyAlarmEquipment 声光报警设备
     * @return 结果
     */
    @Override
    public int updateAqyAlarmEquipment(AqyAlarmEquipment aqyAlarmEquipment)
    {
        return aqyAlarmEquipmentMapper.updateAqyAlarmEquipment(aqyAlarmEquipment);
    }

    /**
     * 批量删除声光报警设备
     *
     * @param ids 需要删除的声光报警设备主键
     * @return 结果
     */
    @Override
    public int deleteAqyAlarmEquipmentByIds(Long[] ids)
    {
        return aqyAlarmEquipmentMapper.deleteAqyAlarmEquipmentByIds(ids);
    }

    /**
     * 删除声光报警设备信息
     *
     * @param id 声光报警设备主键
     * @return 结果
     */
    @Override
    public int deleteAqyAlarmEquipmentById(Long id)
    {
        return aqyAlarmEquipmentMapper.deleteAqyAlarmEquipmentById(id);
    }
}
