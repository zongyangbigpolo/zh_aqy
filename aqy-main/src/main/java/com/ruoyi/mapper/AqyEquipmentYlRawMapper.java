package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentYlRaw;
import com.ruoyi.common.core.domain.aqy.AqyRawReport;

/**
 * 雨量监测设备上传数据记录Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyEquipmentYlRawMapper
{
    /**
     * 查询雨量监测设备上传数据记录
     *
     * @param id 雨量监测设备上传数据记录主键
     * @return 雨量监测设备上传数据记录
     */
    public AqyEquipmentYlRaw selectAqyEquipmentYlRawById(Long id);

    /**
     * 查询雨量监测设备上传数据记录列表
     *
     * @param aqyEquipmentYlRaw 雨量监测设备上传数据记录
     * @return 雨量监测设备上传数据记录集合
     */
    public List<AqyEquipmentYlRaw> selectAqyEquipmentYlRawList(AqyEquipmentYlRaw aqyEquipmentYlRaw);

    /**
     * 新增雨量监测设备上传数据记录
     *
     * @param aqyEquipmentYlRaw 雨量监测设备上传数据记录
     * @return 结果
     */
    public int insertAqyEquipmentYlRaw(AqyEquipmentYlRaw aqyEquipmentYlRaw);

    /**
     * 修改雨量监测设备上传数据记录
     *
     * @param aqyEquipmentYlRaw 雨量监测设备上传数据记录
     * @return 结果
     */
    public int updateAqyEquipmentYlRaw(AqyEquipmentYlRaw aqyEquipmentYlRaw);

    /**
     * 删除雨量监测设备上传数据记录
     *
     * @param id 雨量监测设备上传数据记录主键
     * @return 结果
     */
    public int deleteAqyEquipmentYlRawById(Long id);

    /**
     * 批量删除雨量监测设备上传数据记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentYlRawByIds(Long[] ids);

    List<AqyEquipmentYlRaw> selectYlRawListForReport(AqyRawReport rawReport);

    List<AqyEquipmentYlRaw> listRawForCharts(AqyEquipmentYlRaw aqyEquipmentYlRaw);

    List<AqyEquipmentYlRaw> selectLastLimitData(AqyEquipmentYlRaw aqyEquipmentYlRaw);
}
