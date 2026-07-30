package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.core.domain.aqy.Vo.AqyEqmtAlarmVo;
import org.apache.ibatis.annotations.Param;

/**
 * 数据采集设备Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyEquipmentMapper
{
    /**
     * 查询数据采集设备
     *
     * @param id 数据采集设备主键
     * @return 数据采集设备
     */
    public AqyEquipment selectAqyEquipmentById(Long id);

    /**
     * 查询数据采集设备列表
     *
     * @param aqyEquipment 数据采集设备
     * @return 数据采集设备集合
     */
    public List<AqyEquipment> selectAqyEquipmentList(AqyEquipment aqyEquipment);

    /**
     * 新增数据采集设备
     *
     * @param aqyEquipment 数据采集设备
     * @return 结果
     */
    public int insertAqyEquipment(AqyEquipment aqyEquipment);

    /**
     * 修改数据采集设备
     *
     * @param aqyEquipment 数据采集设备
     * @return 结果
     */
    public int updateAqyEquipment(AqyEquipment aqyEquipment);

    /**
     * 删除数据采集设备
     *
     * @param id 数据采集设备主键
     * @return 结果
     */
    public int deleteAqyEquipmentById(Long id);

    /**
     * 批量删除数据采集设备
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentByIds(Long[] ids);

    List<AqyEquipment> selectAqyEquipmentByCode(@Param("eqmtCode") String eqmtCode);

    List<AqyEqmtAlarmVo> selectAqyEquipmentAlarmStatusList(AqyEquipment aqyEquipment);

    List<AqyEqmtAlarmVo> selectAqyEquipmentListForReport(AqyEquipment aqyEquipment);

    List<AqyEquipment> selectAqyEqmtsByType(@Param("eqmtType") String eqmtType);
}
