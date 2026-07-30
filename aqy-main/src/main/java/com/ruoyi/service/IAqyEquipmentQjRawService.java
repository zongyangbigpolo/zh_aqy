package com.ruoyi.service;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentQjRaw;
import com.ruoyi.common.core.domain.aqy.AqyRawReport;

/**
 * 倾角监测设备上传数据记录Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyEquipmentQjRawService
{
    /**
     * 查询倾角监测设备上传数据记录
     *
     * @param id 倾角监测设备上传数据记录主键
     * @return 倾角监测设备上传数据记录
     */
    public AqyEquipmentQjRaw selectAqyEquipmentQjRawById(Long id);

    /**
     * 查询倾角监测设备上传数据记录列表
     *
     * @param aqyEquipmentQjRaw 倾角监测设备上传数据记录
     * @return 倾角监测设备上传数据记录集合
     */
    public List<AqyEquipmentQjRaw> selectAqyEquipmentQjRawList(AqyEquipmentQjRaw aqyEquipmentQjRaw);

    /**
     * 新增倾角监测设备上传数据记录
     *
     * @param aqyEquipmentQjRaw 倾角监测设备上传数据记录
     * @return 结果
     */
    public int insertAqyEquipmentQjRaw(AqyEquipmentQjRaw aqyEquipmentQjRaw);

    /**
     * 修改倾角监测设备上传数据记录
     *
     * @param aqyEquipmentQjRaw 倾角监测设备上传数据记录
     * @return 结果
     */
    public int updateAqyEquipmentQjRaw(AqyEquipmentQjRaw aqyEquipmentQjRaw);

    /**
     * 批量删除倾角监测设备上传数据记录
     *
     * @param ids 需要删除的倾角监测设备上传数据记录主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentQjRawByIds(Long[] ids);

    /**
     * 删除倾角监测设备上传数据记录信息
     *
     * @param id 倾角监测设备上传数据记录主键
     * @return 结果
     */
    public int deleteAqyEquipmentQjRawById(Long id);

    List<AqyEquipmentQjRaw> selectQjRawListForReport(AqyRawReport rawReport);

    List<AqyEquipmentQjRaw> listRawForCharts(AqyEquipmentQjRaw aqyEquipmentQjRaw);

    AqyEquipmentQjRaw selectLastDataByEqmtId(Long id);
}
