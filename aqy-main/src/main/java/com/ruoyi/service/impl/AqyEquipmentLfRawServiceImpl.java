package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentYlRaw;
import com.ruoyi.common.core.domain.aqy.AqyRawReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyEquipmentLfRawMapper;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentLfRaw;
import com.ruoyi.service.IAqyEquipmentLfRawService;

/**
 * 裂缝监测设备上传数据记录Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyEquipmentLfRawServiceImpl implements IAqyEquipmentLfRawService
{
    @Autowired
    private AqyEquipmentLfRawMapper aqyEquipmentLfRawMapper;

    /**
     * 查询裂缝监测设备上传数据记录
     *
     * @param id 裂缝监测设备上传数据记录主键
     * @return 裂缝监测设备上传数据记录
     */
    @Override
    public AqyEquipmentLfRaw selectAqyEquipmentLfRawById(Long id)
    {
        return aqyEquipmentLfRawMapper.selectAqyEquipmentLfRawById(id);
    }

    /**
     * 查询裂缝监测设备上传数据记录列表
     *
     * @param aqyEquipmentLfRaw 裂缝监测设备上传数据记录
     * @return 裂缝监测设备上传数据记录
     */
    @Override
    public List<AqyEquipmentLfRaw> selectAqyEquipmentLfRawList(AqyEquipmentLfRaw aqyEquipmentLfRaw)
    {
        return aqyEquipmentLfRawMapper.selectAqyEquipmentLfRawList(aqyEquipmentLfRaw);
    }

    /**
     * 新增裂缝监测设备上传数据记录
     *
     * @param aqyEquipmentLfRaw 裂缝监测设备上传数据记录
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentLfRaw(AqyEquipmentLfRaw aqyEquipmentLfRaw)
    {
        return aqyEquipmentLfRawMapper.insertAqyEquipmentLfRaw(aqyEquipmentLfRaw);
    }

    /**
     * 修改裂缝监测设备上传数据记录
     *
     * @param aqyEquipmentLfRaw 裂缝监测设备上传数据记录
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentLfRaw(AqyEquipmentLfRaw aqyEquipmentLfRaw)
    {
        return aqyEquipmentLfRawMapper.updateAqyEquipmentLfRaw(aqyEquipmentLfRaw);
    }

    /**
     * 批量删除裂缝监测设备上传数据记录
     *
     * @param ids 需要删除的裂缝监测设备上传数据记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentLfRawByIds(Long[] ids)
    {
        return aqyEquipmentLfRawMapper.deleteAqyEquipmentLfRawByIds(ids);
    }

    /**
     * 删除裂缝监测设备上传数据记录信息
     *
     * @param id 裂缝监测设备上传数据记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentLfRawById(Long id)
    {
        return aqyEquipmentLfRawMapper.deleteAqyEquipmentLfRawById(id);
    }

    @Override
    public List<AqyEquipmentLfRaw> selectLfRawListForReport(AqyRawReport rawReport) {
        return aqyEquipmentLfRawMapper.selectLfRawListForReport(rawReport);
    }

    @Override
    public List<AqyEquipmentLfRaw> listRawForCharts(AqyEquipmentLfRaw aqyEquipmentLfRaw) {
        return aqyEquipmentLfRawMapper.listRawForCharts(aqyEquipmentLfRaw);
    }

    @Override
    public AqyEquipmentLfRaw selectLastDataByEqmtId(Long id) {
            return aqyEquipmentLfRawMapper.selectLastDataByEqmtId(id);
    }
}
