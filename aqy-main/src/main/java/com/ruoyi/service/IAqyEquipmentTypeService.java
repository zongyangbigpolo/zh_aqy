package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentType;

import java.util.List;

/**
 * 设备类型Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyEquipmentTypeService
{
    /**
     * 查询设备类型
     *
     * @param id 设备类型主键
     * @return 设备类型
     */
    public AqyEquipmentType selectAqyEquipmentTypeById(Long id);

    /**
     * 查询设备类型列表
     *
     * @param aqyEquipmentType 设备类型
     * @return 设备类型集合
     */
    public List<AqyEquipmentType> selectAqyEquipmentTypeList(AqyEquipmentType aqyEquipmentType);

    /**
     * 新增设备类型
     *
     * @param aqyEquipmentType 设备类型
     * @return 结果
     */
    public int insertAqyEquipmentType(AqyEquipmentType aqyEquipmentType);

    /**
     * 修改设备类型
     *
     * @param aqyEquipmentType 设备类型
     * @return 结果
     */
    public int updateAqyEquipmentType(AqyEquipmentType aqyEquipmentType);

    /**
     * 批量删除设备类型
     *
     * @param ids 需要删除的设备类型主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentTypeByIds(Long[] ids);

    /**
     * 删除设备类型信息
     *
     * @param id 设备类型主键
     * @return 结果
     */
    public int deleteAqyEquipmentTypeById(Long id);

    public AqyEquipmentType selectAqyEquipmentTypeBySymbol(String eqmtTypeSymbol);
}
