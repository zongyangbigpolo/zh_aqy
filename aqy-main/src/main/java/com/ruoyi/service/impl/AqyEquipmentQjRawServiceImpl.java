package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyRawReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyEquipmentQjRawMapper;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentQjRaw;
import com.ruoyi.service.IAqyEquipmentQjRawService;

/**
 * 倾角监测设备上传数据记录Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyEquipmentQjRawServiceImpl implements IAqyEquipmentQjRawService
{
    @Autowired
    private AqyEquipmentQjRawMapper aqyEquipmentQjRawMapper;

    /**
     * 查询倾角监测设备上传数据记录
     *
     * @param id 倾角监测设备上传数据记录主键
     * @return 倾角监测设备上传数据记录
     */
    @Override
    public AqyEquipmentQjRaw selectAqyEquipmentQjRawById(Long id)
    {
        return aqyEquipmentQjRawMapper.selectAqyEquipmentQjRawById(id);
    }

    /**
     * 查询倾角监测设备上传数据记录列表
     *
     * @param aqyEquipmentQjRaw 倾角监测设备上传数据记录
     * @return 倾角监测设备上传数据记录
     */
    @Override
    public List<AqyEquipmentQjRaw> selectAqyEquipmentQjRawList(AqyEquipmentQjRaw aqyEquipmentQjRaw)
    {
        return aqyEquipmentQjRawMapper.selectAqyEquipmentQjRawList(aqyEquipmentQjRaw);
    }

    /**
     * 新增倾角监测设备上传数据记录
     *
     * @param aqyEquipmentQjRaw 倾角监测设备上传数据记录
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentQjRaw(AqyEquipmentQjRaw aqyEquipmentQjRaw)
    {
        return aqyEquipmentQjRawMapper.insertAqyEquipmentQjRaw(aqyEquipmentQjRaw);
    }

    /**
     * 修改倾角监测设备上传数据记录
     *
     * @param aqyEquipmentQjRaw 倾角监测设备上传数据记录
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentQjRaw(AqyEquipmentQjRaw aqyEquipmentQjRaw)
    {
        return aqyEquipmentQjRawMapper.updateAqyEquipmentQjRaw(aqyEquipmentQjRaw);
    }

    /**
     * 批量删除倾角监测设备上传数据记录
     *
     * @param ids 需要删除的倾角监测设备上传数据记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentQjRawByIds(Long[] ids)
    {
        return aqyEquipmentQjRawMapper.deleteAqyEquipmentQjRawByIds(ids);
    }

    /**
     * 删除倾角监测设备上传数据记录信息
     *
     * @param id 倾角监测设备上传数据记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentQjRawById(Long id)
    {
        return aqyEquipmentQjRawMapper.deleteAqyEquipmentQjRawById(id);
    }

    @Override
    public List<AqyEquipmentQjRaw> selectQjRawListForReport(AqyRawReport rawReport) {
        return aqyEquipmentQjRawMapper.selectQjRawListForReport(rawReport);
    }

    @Override
    public List<AqyEquipmentQjRaw> listRawForCharts(AqyEquipmentQjRaw aqyEquipmentQjRaw) {
        return aqyEquipmentQjRawMapper.listRawForCharts(aqyEquipmentQjRaw);
    }

    @Override
    public AqyEquipmentQjRaw selectLastDataByEqmtId(Long id) {
            return aqyEquipmentQjRawMapper.selectLastDataByEqmtId(id);
    }
}
