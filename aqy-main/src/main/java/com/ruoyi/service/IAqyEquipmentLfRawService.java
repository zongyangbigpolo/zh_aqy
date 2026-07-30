package com.ruoyi.service;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentLfRaw;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentWyRaw;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentYlRaw;
import com.ruoyi.common.core.domain.aqy.AqyRawReport;

/**
 * 裂缝监测设备上传数据记录Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyEquipmentLfRawService
{
    /**
     * 查询裂缝监测设备上传数据记录
     *
     * @param id 裂缝监测设备上传数据记录主键
     * @return 裂缝监测设备上传数据记录
     */
    public AqyEquipmentLfRaw selectAqyEquipmentLfRawById(Long id);

    /**
     * 查询裂缝监测设备上传数据记录列表
     *
     * @param aqyEquipmentLfRaw 裂缝监测设备上传数据记录
     * @return 裂缝监测设备上传数据记录集合
     */
    public List<AqyEquipmentLfRaw> selectAqyEquipmentLfRawList(AqyEquipmentLfRaw aqyEquipmentLfRaw);

    /**
     * 新增裂缝监测设备上传数据记录
     *
     * @param aqyEquipmentLfRaw 裂缝监测设备上传数据记录
     * @return 结果
     */
    public int insertAqyEquipmentLfRaw(AqyEquipmentLfRaw aqyEquipmentLfRaw);

    /**
     * 修改裂缝监测设备上传数据记录
     *
     * @param aqyEquipmentLfRaw 裂缝监测设备上传数据记录
     * @return 结果
     */
    public int updateAqyEquipmentLfRaw(AqyEquipmentLfRaw aqyEquipmentLfRaw);

    /**
     * 批量删除裂缝监测设备上传数据记录
     *
     * @param ids 需要删除的裂缝监测设备上传数据记录主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentLfRawByIds(Long[] ids);

    /**
     * 删除裂缝监测设备上传数据记录信息
     *
     * @param id 裂缝监测设备上传数据记录主键
     * @return 结果
     */
    public int deleteAqyEquipmentLfRawById(Long id);

    List<AqyEquipmentLfRaw> selectLfRawListForReport(AqyRawReport rawReport);

    List<AqyEquipmentLfRaw> listRawForCharts(AqyEquipmentLfRaw aqyEquipmentLfRaw);

    AqyEquipmentLfRaw selectLastDataByEqmtId(Long id);
}
