package com.ruoyi.service;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentTarget;


/**
 * 靶标管理Service接口
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
public interface IAqyEquipmentTargetService 
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
     * 批量删除靶标管理
     * 
     * @param ids 需要删除的靶标管理主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentTargetByIds(Long[] ids);

    /**
     * 删除靶标管理信息
     * 
     * @param id 靶标管理主键
     * @return 结果
     */
    public int deleteAqyEquipmentTargetById(Long id);
}
