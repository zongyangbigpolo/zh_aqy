package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyRawReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyEquipmentYlRawMapper;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentYlRaw;
import com.ruoyi.service.IAqyEquipmentYlRawService;

/**
 * 雨量监测设备上传数据记录Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyEquipmentYlRawServiceImpl implements IAqyEquipmentYlRawService
{
    @Autowired
    private AqyEquipmentYlRawMapper aqyEquipmentYlRawMapper;

    /**
     * 查询雨量监测设备上传数据记录
     *
     * @param id 雨量监测设备上传数据记录主键
     * @return 雨量监测设备上传数据记录
     */
    @Override
    public AqyEquipmentYlRaw selectAqyEquipmentYlRawById(Long id)
    {
        return aqyEquipmentYlRawMapper.selectAqyEquipmentYlRawById(id);
    }

    /**
     * 查询雨量监测设备上传数据记录列表
     *
     * @param aqyEquipmentYlRaw 雨量监测设备上传数据记录
     * @return 雨量监测设备上传数据记录
     */
    @Override
    public List<AqyEquipmentYlRaw> selectAqyEquipmentYlRawList(AqyEquipmentYlRaw aqyEquipmentYlRaw)
    {
        return aqyEquipmentYlRawMapper.selectAqyEquipmentYlRawList(aqyEquipmentYlRaw);
    }

    /**
     * 新增雨量监测设备上传数据记录
     *
     * @param aqyEquipmentYlRaw 雨量监测设备上传数据记录
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentYlRaw(AqyEquipmentYlRaw aqyEquipmentYlRaw)
    {
        return aqyEquipmentYlRawMapper.insertAqyEquipmentYlRaw(aqyEquipmentYlRaw);
    }

    /**
     * 修改雨量监测设备上传数据记录
     *
     * @param aqyEquipmentYlRaw 雨量监测设备上传数据记录
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentYlRaw(AqyEquipmentYlRaw aqyEquipmentYlRaw)
    {
        return aqyEquipmentYlRawMapper.updateAqyEquipmentYlRaw(aqyEquipmentYlRaw);
    }

    /**
     * 批量删除雨量监测设备上传数据记录
     *
     * @param ids 需要删除的雨量监测设备上传数据记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentYlRawByIds(Long[] ids)
    {
        return aqyEquipmentYlRawMapper.deleteAqyEquipmentYlRawByIds(ids);
    }

    /**
     * 删除雨量监测设备上传数据记录信息
     *
     * @param id 雨量监测设备上传数据记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentYlRawById(Long id)
    {
        return aqyEquipmentYlRawMapper.deleteAqyEquipmentYlRawById(id);
    }

    @Override
    public List<AqyEquipmentYlRaw> selectYlRawListForReport(AqyRawReport rawReport) {
        return aqyEquipmentYlRawMapper.selectYlRawListForReport(rawReport);
    }

    @Override
    public List<AqyEquipmentYlRaw> listRawForCharts(AqyEquipmentYlRaw aqyEquipmentYlRaw) {
        return aqyEquipmentYlRawMapper.listRawForCharts(aqyEquipmentYlRaw);
    }

    @Override
    public List<AqyEquipmentYlRaw> selectLastLimitData(AqyEquipmentYlRaw aqyEquipmentYlRaw) {
        return aqyEquipmentYlRawMapper.selectLastLimitData(aqyEquipmentYlRaw);
    }
}
