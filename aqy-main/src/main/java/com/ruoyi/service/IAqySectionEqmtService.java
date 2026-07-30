package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqySectionEqmt;

import java.util.List;


/**
 * 断面配置的监测设备类型Service接口
 *
 * @author MXJ
 * @date 2024-09-11
 */
public interface IAqySectionEqmtService
{
    /**
     * 查询断面配置的监测设备类型
     *
     * @param id 断面配置的监测设备类型主键
     * @return 断面配置的监测设备类型
     */
    public AqySectionEqmt selectAqySectionEqmtById(Long id);

    /**
     * 查询断面配置的监测设备类型列表
     *
     * @param aqySectionEqmt 断面配置的监测设备类型
     * @return 断面配置的监测设备类型集合
     */
    public List<AqySectionEqmt> selectAqySectionEqmtList(AqySectionEqmt aqySectionEqmt);

    /**
     * 新增断面配置的监测设备类型
     *
     * @param aqySectionEqmt 断面配置的监测设备类型
     * @return 结果
     */
    public int insertAqySectionEqmt(AqySectionEqmt aqySectionEqmt);

    /**
     * 修改断面配置的监测设备类型
     *
     * @param aqySectionEqmt 断面配置的监测设备类型
     * @return 结果
     */
    public int updateAqySectionEqmt(AqySectionEqmt aqySectionEqmt);

    /**
     * 批量删除断面配置的监测设备类型
     *
     * @param ids 需要删除的断面配置的监测设备类型主键集合
     * @return 结果
     */
    public int deleteAqySectionEqmtByIds(Long[] ids);

    /**
     * 删除断面配置的监测设备类型信息
     *
     * @param id 断面配置的监测设备类型主键
     * @return 结果
     */
    public int deleteAqySectionEqmtById(Long id);

    List<String> listEqmtTypeByProjectId(Long projectId);
}
