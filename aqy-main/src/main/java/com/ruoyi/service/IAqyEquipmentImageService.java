package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentImage;

import java.util.List;

/**
 * 监测设备抓取照片记录Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyEquipmentImageService
{
    /**
     * 查询监测设备抓取照片记录
     *
     * @param id 监测设备抓取照片记录主键
     * @return 监测设备抓取照片记录
     */
    public AqyEquipmentImage selectAqyEquipmentImageById(Long id);

    /**
     * 查询监测设备抓取照片记录列表
     *
     * @param aqyEquipmentImage 监测设备抓取照片记录
     * @return 监测设备抓取照片记录集合
     */
    public List<AqyEquipmentImage> selectAqyEquipmentImageList(AqyEquipmentImage aqyEquipmentImage);

    /**
     * 新增监测设备抓取照片记录
     *
     * @param aqyEquipmentImage 监测设备抓取照片记录
     * @return 结果
     */
    public int insertAqyEquipmentImage(AqyEquipmentImage aqyEquipmentImage);

    /**
     * 修改监测设备抓取照片记录
     *
     * @param aqyEquipmentImage 监测设备抓取照片记录
     * @return 结果
     */
    public int updateAqyEquipmentImage(AqyEquipmentImage aqyEquipmentImage);

    /**
     * 批量删除监测设备抓取照片记录
     *
     * @param ids 需要删除的监测设备抓取照片记录主键集合
     * @return 结果
     */
    public int deleteAqyEquipmentImageByIds(Long[] ids);

    /**
     * 删除监测设备抓取照片记录信息
     *
     * @param id 监测设备抓取照片记录主键
     * @return 结果
     */
    public int deleteAqyEquipmentImageById(Long id);
}
