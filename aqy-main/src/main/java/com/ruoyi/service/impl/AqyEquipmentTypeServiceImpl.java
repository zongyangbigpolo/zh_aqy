package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentType;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyEquipmentTypeMapper;
import com.ruoyi.service.IAqyEquipmentTypeService;

/**
 * 设备类型Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyEquipmentTypeServiceImpl implements IAqyEquipmentTypeService
{
    @Autowired
    private AqyEquipmentTypeMapper aqyEquipmentTypeMapper;

    /**
     * 查询设备类型
     *
     * @param id 设备类型主键
     * @return 设备类型
     */
    @Override
    public AqyEquipmentType selectAqyEquipmentTypeById(Long id)
    {
        return aqyEquipmentTypeMapper.selectAqyEquipmentTypeById(id);
    }

    /**
     * 查询设备类型列表
     *
     * @param aqyEquipmentType 设备类型
     * @return 设备类型
     */
    @Override
    public List<AqyEquipmentType> selectAqyEquipmentTypeList(AqyEquipmentType aqyEquipmentType)
    {
        return aqyEquipmentTypeMapper.selectAqyEquipmentTypeList(aqyEquipmentType);
    }

    /**
     * 新增设备类型
     *
     * @param aqyEquipmentType 设备类型
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentType(AqyEquipmentType aqyEquipmentType)
    {
        aqyEquipmentType.setCreateTime(DateUtils.getNowDate());
        return aqyEquipmentTypeMapper.insertAqyEquipmentType(aqyEquipmentType);
    }

    /**
     * 修改设备类型
     *
     * @param aqyEquipmentType 设备类型
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentType(AqyEquipmentType aqyEquipmentType)
    {
        return aqyEquipmentTypeMapper.updateAqyEquipmentType(aqyEquipmentType);
    }

    /**
     * 批量删除设备类型
     *
     * @param ids 需要删除的设备类型主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentTypeByIds(Long[] ids)
    {
        return aqyEquipmentTypeMapper.deleteAqyEquipmentTypeByIds(ids);
    }

    /**
     * 删除设备类型信息
     *
     * @param id 设备类型主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentTypeById(Long id)
    {
        return aqyEquipmentTypeMapper.deleteAqyEquipmentTypeById(id);
    }

    @Override
    public AqyEquipmentType selectAqyEquipmentTypeBySymbol(String eqmtTypeSymbol) {
        return aqyEquipmentTypeMapper.selectAqyEquipmentTypeBySymbol(eqmtTypeSymbol);
    }
}
