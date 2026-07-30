package com.ruoyi.service;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentWyRaw;
import com.ruoyi.common.core.domain.aqy.AqyRawReport;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hpsf.Decimal;

/**
 * 位移监测设备上传数据记录Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyEquipmentWyRawService {
    /**
     * 查询位移监测设备上传数据记录
     *
     * @param id 位移监测设备上传数据记录主键
     * @return 位移监测设备上传数据记录
     */
    public AqyEquipmentWyRaw selectAqyEquipmentWyRawById(Long id);

    /**
     * 查询位移监测设备上传数据记录列表
     *
     * @param aqyEquipmentWyRaw 位移监测设备上传数据记录
     * @return 位移监测设备上传数据记录集合
     */
    public List<AqyEquipmentWyRaw> selectAqyEquipmentWyRawList(AqyEquipmentWyRaw aqyEquipmentWyRaw);

    /**
     * 新增位移监测设备上传数据记录
     *
     * @param aqyEquipmentWyRaw 位移监测设备上传数据记录
     * @return 结果
     */
    public int insertAqyEquipmentWyRaw(AqyEquipmentWyRaw aqyEquipmentWyRaw);

    /**
     * 修改位移监测设备上传数据记录
     *
     * @param aqyEquipmentWyRaw 位移监测设备上传数据记录
     * @return 结果
     */
    public int updateAqyEquipmentWyRaw(AqyEquipmentWyRaw aqyEquipmentWyRaw);

    /**
     * 批量删除位移监测设备上传数据记录
     *
     * @param ids 需要删除的位移监测设备上传数据记录主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentWyRawByIds(Long[] ids);

    /**
     * 删除位移监测设备上传数据记录信息
     *
     * @param id 位移监测设备上传数据记录主键
     * @return 结果
     */
    public int deleteAqyEquipmentWyRawById(Long id);

    List<AqyEquipmentWyRaw> selectWyRawListForReport(AqyRawReport rawReport);

    List<AqyEquipmentWyRaw> listRawForCharts(AqyEquipmentWyRaw aqyEquipmentWyRaw);

    int isCheckData(@Param("eqmtCode") String eqmtCode, @Param("catchTime") Long catchTime);

    List<AqyEquipmentWyRaw> selectRawForCharts(AqyEquipmentWyRaw aqyEquipmentWyRaw);

    AqyEquipmentWyRaw selectLastDataByEqmtId(Long id);
}
