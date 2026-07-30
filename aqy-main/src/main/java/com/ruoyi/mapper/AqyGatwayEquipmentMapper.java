package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyGatwayEquipment;

/**
 * 智能网关设备Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyGatwayEquipmentMapper
{
    /**
     * 查询智能网关设备
     *
     * @param id 智能网关设备主键
     * @return 智能网关设备
     */
    public AqyGatwayEquipment selectAqyGatwayEquipmentById(Long id);

    /**
     * 查询智能网关设备列表
     *
     * @param aqyGatwayEquipment 智能网关设备
     * @return 智能网关设备集合
     */
    public List<AqyGatwayEquipment> selectAqyGatwayEquipmentList(AqyGatwayEquipment aqyGatwayEquipment);

    /**
     * 新增智能网关设备
     *
     * @param aqyGatwayEquipment 智能网关设备
     * @return 结果
     */
    public int insertAqyGatwayEquipment(AqyGatwayEquipment aqyGatwayEquipment);

    /**
     * 修改智能网关设备
     *
     * @param aqyGatwayEquipment 智能网关设备
     * @return 结果
     */
    public int updateAqyGatwayEquipment(AqyGatwayEquipment aqyGatwayEquipment);

    /**
     * 删除智能网关设备
     *
     * @param id 智能网关设备主键
     * @return 结果
     */
    public int deleteAqyGatwayEquipmentById(Long id);

    /**
     * 批量删除智能网关设备
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyGatwayEquipmentByIds(Long[] ids);

    AqyGatwayEquipment selectAqyGatwayEquipmentByGatwayId(Long gatwayId);
}
