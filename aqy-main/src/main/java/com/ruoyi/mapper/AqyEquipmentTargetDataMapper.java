package com.ruoyi.mapper;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentTargetData;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 靶标数据Mapper接口
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
public interface AqyEquipmentTargetDataMapper 
{
    /**
     * 查询靶标数据
     * 
     * @param id 靶标数据主键
     * @return 靶标数据
     */
    public AqyEquipmentTargetData selectAqyEquipmentTargetDataById(Long id);

    /**
     * 查询靶标数据列表
     * 
     * @param aqyEquipmentTargetData 靶标数据
     * @return 靶标数据集合
     */
    public List<AqyEquipmentTargetData> selectAqyEquipmentTargetDataList(AqyEquipmentTargetData aqyEquipmentTargetData);

    /**
     * 新增靶标数据
     * 
     * @param aqyEquipmentTargetData 靶标数据
     * @return 结果
     */
    public int insertAqyEquipmentTargetData(AqyEquipmentTargetData aqyEquipmentTargetData);

    /**
     * 修改靶标数据
     * 
     * @param aqyEquipmentTargetData 靶标数据
     * @return 结果
     */
    public int updateAqyEquipmentTargetData(AqyEquipmentTargetData aqyEquipmentTargetData);

    /**
     * 删除靶标数据
     * 
     * @param id 靶标数据主键
     * @return 结果
     */
    public int deleteAqyEquipmentTargetDataById(Long id);

    /**
     * 批量删除靶标数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentTargetDataByIds(Long[] ids);

    int isCheckData(@Param("measItemId") Long measItemId, @Param("catchTime")  String catchTime);
}
