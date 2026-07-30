package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyCamera;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyCameraMapper;
import com.ruoyi.service.IAqyCameraService;

/**
 * 监控摄像头Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyCameraServiceImpl implements IAqyCameraService
{
    @Autowired
    private AqyCameraMapper aqyCameraMapper;

    /**
     * 查询监控摄像头
     *
     * @param id 监控摄像头主键
     * @return 监控摄像头
     */
    @Override
    public AqyCamera selectAqyCameraById(Long id)
    {
        return aqyCameraMapper.selectAqyCameraById(id);
    }

    /**
     * 查询监控摄像头列表
     *
     * @param aqyCamera 监控摄像头
     * @return 监控摄像头
     */
    @Override
    public List<AqyCamera> selectAqyCameraList(AqyCamera aqyCamera)
    {
        return aqyCameraMapper.selectAqyCameraList(aqyCamera);
    }

    /**
     * 新增监控摄像头
     *
     * @param aqyCamera 监控摄像头
     * @return 结果
     */
    @Override
    public int insertAqyCamera(AqyCamera aqyCamera)
    {
        aqyCamera.setCreateTime(DateUtils.getNowDate());
        return aqyCameraMapper.insertAqyCamera(aqyCamera);
    }

    /**
     * 修改监控摄像头
     *
     * @param aqyCamera 监控摄像头
     * @return 结果
     */
    @Override
    public int updateAqyCamera(AqyCamera aqyCamera)
    {
        return aqyCameraMapper.updateAqyCamera(aqyCamera);
    }

    /**
     * 批量删除监控摄像头
     *
     * @param ids 需要删除的监控摄像头主键
     * @return 结果
     */
    @Override
    public int deleteAqyCameraByIds(Long[] ids)
    {
        return aqyCameraMapper.deleteAqyCameraByIds(ids);
    }

    /**
     * 删除监控摄像头信息
     *
     * @param id 监控摄像头主键
     * @return 结果
     */
    @Override
    public int deleteAqyCameraById(Long id)
    {
        return aqyCameraMapper.deleteAqyCameraById(id);
    }
}
