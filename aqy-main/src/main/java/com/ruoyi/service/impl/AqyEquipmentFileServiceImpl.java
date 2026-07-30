package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentFile;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyEquipmentFileMapper;
import com.ruoyi.service.IAqyEquipmentFileService;

/**
 * 采集设备的证书文件Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyEquipmentFileServiceImpl implements IAqyEquipmentFileService
{
    @Autowired
    private AqyEquipmentFileMapper aqyEquipmentFileMapper;

    /**
     * 查询采集设备的证书文件
     *
     * @param id 采集设备的证书文件主键
     * @return 采集设备的证书文件
     */
    @Override
    public AqyEquipmentFile selectAqyEquipmentFileById(Long id)
    {
        return aqyEquipmentFileMapper.selectAqyEquipmentFileById(id);
    }

    /**
     * 查询采集设备的证书文件列表
     *
     * @param aqyEquipmentFile 采集设备的证书文件
     * @return 采集设备的证书文件
     */
    @Override
    public List<AqyEquipmentFile> selectAqyEquipmentFileList(AqyEquipmentFile aqyEquipmentFile)
    {
        return aqyEquipmentFileMapper.selectAqyEquipmentFileList(aqyEquipmentFile);
    }

    /**
     * 新增采集设备的证书文件
     *
     * @param aqyEquipmentFile 采集设备的证书文件
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentFile(AqyEquipmentFile aqyEquipmentFile)
    {
        aqyEquipmentFile.setCreateTime(DateUtils.getNowDate());
        return aqyEquipmentFileMapper.insertAqyEquipmentFile(aqyEquipmentFile);
    }

    /**
     * 修改采集设备的证书文件
     *
     * @param aqyEquipmentFile 采集设备的证书文件
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentFile(AqyEquipmentFile aqyEquipmentFile)
    {
        return aqyEquipmentFileMapper.updateAqyEquipmentFile(aqyEquipmentFile);
    }

    /**
     * 批量删除采集设备的证书文件
     *
     * @param ids 需要删除的采集设备的证书文件主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentFileByIds(Long[] ids)
    {
        return aqyEquipmentFileMapper.deleteAqyEquipmentFileByIds(ids);
    }

    /**
     * 删除采集设备的证书文件信息
     *
     * @param id 采集设备的证书文件主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentFileById(Long id)
    {
        return aqyEquipmentFileMapper.deleteAqyEquipmentFileById(id);
    }
}
