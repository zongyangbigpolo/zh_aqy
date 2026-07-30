package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyRawReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyEquipmentWyRawMapper;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentWyRaw;
import com.ruoyi.service.IAqyEquipmentWyRawService;

/**
 * 位移监测设备上传数据记录Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyEquipmentWyRawServiceImpl implements IAqyEquipmentWyRawService {
    @Autowired
    private AqyEquipmentWyRawMapper aqyEquipmentWyRawMapper;

    /**
     * 查询位移监测设备上传数据记录
     *
     * @param id 位移监测设备上传数据记录主键
     * @return 位移监测设备上传数据记录
     */
    @Override
    public AqyEquipmentWyRaw selectAqyEquipmentWyRawById(Long id) {
        return aqyEquipmentWyRawMapper.selectAqyEquipmentWyRawById(id);
    }

    /**
     * 查询位移监测设备上传数据记录列表
     *
     * @param aqyEquipmentWyRaw 位移监测设备上传数据记录
     * @return 位移监测设备上传数据记录
     */
    @Override
    public List<AqyEquipmentWyRaw> selectAqyEquipmentWyRawList(AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        return aqyEquipmentWyRawMapper.selectAqyEquipmentWyRawList(aqyEquipmentWyRaw);
    }

    /**
     * 新增位移监测设备上传数据记录
     *
     * @param aqyEquipmentWyRaw 位移监测设备上传数据记录
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentWyRaw(AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        return aqyEquipmentWyRawMapper.insertAqyEquipmentWyRaw(aqyEquipmentWyRaw);
    }

    /**
     * 修改位移监测设备上传数据记录
     *
     * @param aqyEquipmentWyRaw 位移监测设备上传数据记录
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentWyRaw(AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        return aqyEquipmentWyRawMapper.updateAqyEquipmentWyRaw(aqyEquipmentWyRaw);
    }

    /**
     * 批量删除位移监测设备上传数据记录
     *
     * @param ids 需要删除的位移监测设备上传数据记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentWyRawByIds(Long[] ids) {
        return aqyEquipmentWyRawMapper.deleteAqyEquipmentWyRawByIds(ids);
    }

    /**
     * 删除位移监测设备上传数据记录信息
     *
     * @param id 位移监测设备上传数据记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentWyRawById(Long id) {
        return aqyEquipmentWyRawMapper.deleteAqyEquipmentWyRawById(id);
    }

    @Override
    public List<AqyEquipmentWyRaw> selectWyRawListForReport(AqyRawReport rawReport) {
        return aqyEquipmentWyRawMapper.selectWyRawListForReport(rawReport);
    }

    @Override
    public List<AqyEquipmentWyRaw> listRawForCharts(AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        return aqyEquipmentWyRawMapper.listRawForCharts(aqyEquipmentWyRaw);
    }

    @Override
    public int isCheckData(String eqmtCode, Long catchTime) {
        return aqyEquipmentWyRawMapper.isCheckData(eqmtCode, catchTime);
    }

    @Override
    public List<AqyEquipmentWyRaw> selectRawForCharts(AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        return aqyEquipmentWyRawMapper.selectRawForCharts(aqyEquipmentWyRaw);
    }

    @Override
    public AqyEquipmentWyRaw selectLastDataByEqmtId(Long id) {
        return aqyEquipmentWyRawMapper.selectLastDataByEqmtId(id);
    }
}
