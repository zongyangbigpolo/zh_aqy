package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqyAlarmEquipment;

import java.util.List;

/**
 * 声光报警设备Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyAlarmEquipmentService
{
    /**
     * 查询声光报警设备
     *
     * @param id 声光报警设备主键
     * @return 声光报警设备
     */
    public AqyAlarmEquipment selectAqyAlarmEquipmentById(Long id);

    /**
     * 查询声光报警设备列表
     *
     * @param aqyAlarmEquipment 声光报警设备
     * @return 声光报警设备集合
     */
    public List<AqyAlarmEquipment> selectAqyAlarmEquipmentList(AqyAlarmEquipment aqyAlarmEquipment);

    /**
     * 新增声光报警设备
     *
     * @param aqyAlarmEquipment 声光报警设备
     * @return 结果
     */
    public int insertAqyAlarmEquipment(AqyAlarmEquipment aqyAlarmEquipment);

    /**
     * 修改声光报警设备
     *
     * @param aqyAlarmEquipment 声光报警设备
     * @return 结果
     */
    public int updateAqyAlarmEquipment(AqyAlarmEquipment aqyAlarmEquipment);

    /**
     * 批量删除声光报警设备
     *
     * @param ids 需要删除的声光报警设备主键集合
     * @return 结果
     */
    public int deleteAqyAlarmEquipmentByIds(Long[] ids);

    /**
     * 删除声光报警设备信息
     *
     * @param id 声光报警设备主键
     * @return 结果
     */
    public int deleteAqyAlarmEquipmentById(Long id);
}
