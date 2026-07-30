package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentFile;

/**
 * 采集设备的证书文件Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyEquipmentFileMapper
{
    /**
     * 查询采集设备的证书文件
     *
     * @param id 采集设备的证书文件主键
     * @return 采集设备的证书文件
     */
    public AqyEquipmentFile selectAqyEquipmentFileById(Long id);

    /**
     * 查询采集设备的证书文件列表
     *
     * @param aqyEquipmentFile 采集设备的证书文件
     * @return 采集设备的证书文件集合
     */
    public List<AqyEquipmentFile> selectAqyEquipmentFileList(AqyEquipmentFile aqyEquipmentFile);

    /**
     * 新增采集设备的证书文件
     *
     * @param aqyEquipmentFile 采集设备的证书文件
     * @return 结果
     */
    public int insertAqyEquipmentFile(AqyEquipmentFile aqyEquipmentFile);

    /**
     * 修改采集设备的证书文件
     *
     * @param aqyEquipmentFile 采集设备的证书文件
     * @return 结果
     */
    public int updateAqyEquipmentFile(AqyEquipmentFile aqyEquipmentFile);

    /**
     * 删除采集设备的证书文件
     *
     * @param id 采集设备的证书文件主键
     * @return 结果
     */
    public int deleteAqyEquipmentFileById(Long id);

    /**
     * 批量删除采集设备的证书文件
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentFileByIds(Long[] ids);
}
