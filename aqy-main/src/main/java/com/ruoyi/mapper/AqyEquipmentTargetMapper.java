package com.ruoyi.mapper;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentTarget;

import java.util.List;


/**
 * 靶标管理Mapper接口
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
public interface AqyEquipmentTargetMapper
{
    /**
     * 查询靶标管理
     * 
     * @param id 靶标管理主键
     * @return 靶标管理
     */
    public AqyEquipmentTarget selectAqyEquipmentTargetById(Long id);

    /**
     * 查询靶标管理列表
     * 
     * @param aqyEquipmentTarget 靶标管理
     * @return 靶标管理集合
     */
    public List<AqyEquipmentTarget> selectAqyEquipmentTargetList(AqyEquipmentTarget aqyEquipmentTarget);

    /**
     * 新增靶标管理
     * 
     * @param aqyEquipmentTarget 靶标管理
     * @return 结果
     */
    public int insertAqyEquipmentTarget(AqyEquipmentTarget aqyEquipmentTarget);

    /**
     * 修改靶标管理
     * 
     * @param aqyEquipmentTarget 靶标管理
     * @return 结果
     */
    public int updateAqyEquipmentTarget(AqyEquipmentTarget aqyEquipmentTarget);

    /**
     * 删除靶标管理
     * 
     * @param id 靶标管理主键
     * @return 结果
     */
    public int deleteAqyEquipmentTargetById(Long id);

    /**
     * 批量删除靶标管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentTargetByIds(Long[] ids);
}
