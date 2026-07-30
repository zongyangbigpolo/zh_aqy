package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqyCamera;

import java.util.List;

/**
 * 监控摄像头Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyCameraService
{
    /**
     * 查询监控摄像头
     *
     * @param id 监控摄像头主键
     * @return 监控摄像头
     */
    public AqyCamera selectAqyCameraById(Long id);

    /**
     * 查询监控摄像头列表
     *
     * @param aqyCamera 监控摄像头
     * @return 监控摄像头集合
     */
    public List<AqyCamera> selectAqyCameraList(AqyCamera aqyCamera);

    /**
     * 新增监控摄像头
     *
     * @param aqyCamera 监控摄像头
     * @return 结果
     */
    public int insertAqyCamera(AqyCamera aqyCamera);

    /**
     * 修改监控摄像头
     *
     * @param aqyCamera 监控摄像头
     * @return 结果
     */
    public int updateAqyCamera(AqyCamera aqyCamera);

    /**
     * 批量删除监控摄像头
     *
     * @param ids 需要删除的监控摄像头主键集合
     * @return 结果
     */
    public int deleteAqyCameraByIds(Long[] ids);

    /**
     * 删除监控摄像头信息
     *
     * @param id 监控摄像头主键
     * @return 结果
     */
    public int deleteAqyCameraById(Long id);
}
