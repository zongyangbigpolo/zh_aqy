package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyCamera;

/**
 * 监控摄像头Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyCameraMapper
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
     * 删除监控摄像头
     *
     * @param id 监控摄像头主键
     * @return 结果
     */
    public int deleteAqyCameraById(Long id);

    /**
     * 批量删除监控摄像头
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyCameraByIds(Long[] ids);
}
