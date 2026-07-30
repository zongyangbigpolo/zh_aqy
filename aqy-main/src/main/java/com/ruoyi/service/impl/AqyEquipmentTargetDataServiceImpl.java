package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentTargetData;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.mapper.AqyEquipmentTargetDataMapper;
import com.ruoyi.service.IAqyEquipmentTargetDataService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 靶标数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
@Service
public class AqyEquipmentTargetDataServiceImpl implements IAqyEquipmentTargetDataService
{
    @Autowired
    private AqyEquipmentTargetDataMapper aqyEquipmentTargetDataMapper;

    /**
     * 查询靶标数据
     * 
     * @param id 靶标数据主键
     * @return 靶标数据
     */
    @Override
    public AqyEquipmentTargetData selectAqyEquipmentTargetDataById(Long id)
    {
        return aqyEquipmentTargetDataMapper.selectAqyEquipmentTargetDataById(id);
    }

    /**
     * 查询靶标数据列表
     * 
     * @param aqyEquipmentTargetData 靶标数据
     * @return 靶标数据
     */
    @Override
    public List<AqyEquipmentTargetData> selectAqyEquipmentTargetDataList(AqyEquipmentTargetData aqyEquipmentTargetData)
    {
        return aqyEquipmentTargetDataMapper.selectAqyEquipmentTargetDataList(aqyEquipmentTargetData);
    }

    /**
     * 新增靶标数据
     * 
     * @param aqyEquipmentTargetData 靶标数据
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentTargetData(AqyEquipmentTargetData aqyEquipmentTargetData)
    {
        aqyEquipmentTargetData.setCreateTime(DateUtils.getNowDate());
        return aqyEquipmentTargetDataMapper.insertAqyEquipmentTargetData(aqyEquipmentTargetData);
    }

    /**
     * 修改靶标数据
     * 
     * @param aqyEquipmentTargetData 靶标数据
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentTargetData(AqyEquipmentTargetData aqyEquipmentTargetData)
    {
        return aqyEquipmentTargetDataMapper.updateAqyEquipmentTargetData(aqyEquipmentTargetData);
    }

    /**
     * 批量删除靶标数据
     * 
     * @param ids 需要删除的靶标数据主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentTargetDataByIds(Long[] ids)
    {
        return aqyEquipmentTargetDataMapper.deleteAqyEquipmentTargetDataByIds(ids);
    }

    /**
     * 删除靶标数据信息
     * 
     * @param id 靶标数据主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentTargetDataById(Long id)
    {
        return aqyEquipmentTargetDataMapper.deleteAqyEquipmentTargetDataById(id);
    }

    @Override
    public int isCheckData( Long measItemId,String catchTime) {
        return aqyEquipmentTargetDataMapper.isCheckData(measItemId, catchTime);
    }
}
