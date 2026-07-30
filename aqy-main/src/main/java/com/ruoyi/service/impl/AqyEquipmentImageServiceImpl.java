package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyEquipmentImageMapper;
import com.ruoyi.service.IAqyEquipmentImageService;

/**
 * 监测设备抓取照片记录Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyEquipmentImageServiceImpl implements IAqyEquipmentImageService
{
    @Autowired
    private AqyEquipmentImageMapper aqyEquipmentImageMapper;

    /**
     * 查询监测设备抓取照片记录
     *
     * @param id 监测设备抓取照片记录主键
     * @return 监测设备抓取照片记录
     */
    @Override
    public AqyEquipmentImage selectAqyEquipmentImageById(Long id)
    {
        return aqyEquipmentImageMapper.selectAqyEquipmentImageById(id);
    }

    /**
     * 查询监测设备抓取照片记录列表
     *
     * @param aqyEquipmentImage 监测设备抓取照片记录
     * @return 监测设备抓取照片记录
     */
    @Override
    public List<AqyEquipmentImage> selectAqyEquipmentImageList(AqyEquipmentImage aqyEquipmentImage)
    {
        return aqyEquipmentImageMapper.selectAqyEquipmentImageList(aqyEquipmentImage);
    }

    /**
     * 新增监测设备抓取照片记录
     *
     * @param aqyEquipmentImage 监测设备抓取照片记录
     * @return 结果
     */
    @Override
    public int insertAqyEquipmentImage(AqyEquipmentImage aqyEquipmentImage)
    {
        return aqyEquipmentImageMapper.insertAqyEquipmentImage(aqyEquipmentImage);
    }

    /**
     * 修改监测设备抓取照片记录
     *
     * @param aqyEquipmentImage 监测设备抓取照片记录
     * @return 结果
     */
    @Override
    public int updateAqyEquipmentImage(AqyEquipmentImage aqyEquipmentImage)
    {
        return aqyEquipmentImageMapper.updateAqyEquipmentImage(aqyEquipmentImage);
    }

    /**
     * 批量删除监测设备抓取照片记录
     *
     * @param ids 需要删除的监测设备抓取照片记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentImageByIds(Long[] ids)
    {
        return aqyEquipmentImageMapper.deleteAqyEquipmentImageByIds(ids);
    }

    /**
     * 删除监测设备抓取照片记录信息
     *
     * @param id 监测设备抓取照片记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentImageById(Long id)
    {
        return aqyEquipmentImageMapper.deleteAqyEquipmentImageById(id);
    }
}
