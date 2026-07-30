package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyAlarmEquipment;

/**
 * 声光报警设备Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyAlarmEquipmentMapper
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
     * 删除声光报警设备
     *
     * @param id 声光报警设备主键
     * @return 结果
     */
    public int deleteAqyAlarmEquipmentById(Long id);

    /**
     * 批量删除声光报警设备
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyAlarmEquipmentByIds(Long[] ids);
}
