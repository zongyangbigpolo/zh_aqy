package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqySectionEqmt;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.mapper.AqySectionEqmtMapper;
import com.ruoyi.service.IAqySectionEqmtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 断面配置的监测设备类型Service业务层处理
 *
 * @author MXJ
 * @date 2024-09-11
 */
@Service
public class AqySectionEqmtServiceImpl implements IAqySectionEqmtService
{
    @Autowired
    private AqySectionEqmtMapper aqySectionEqmtMapper;

    /**
     * 查询断面配置的监测设备类型
     *
     * @param id 断面配置的监测设备类型主键
     * @return 断面配置的监测设备类型
     */
    @Override
    public AqySectionEqmt selectAqySectionEqmtById(Long id)
    {
        return aqySectionEqmtMapper.selectAqySectionEqmtById(id);
    }

    /**
     * 查询断面配置的监测设备类型列表
     *
     * @param aqySectionEqmt 断面配置的监测设备类型
     * @return 断面配置的监测设备类型
     */
    @Override
    public List<AqySectionEqmt> selectAqySectionEqmtList(AqySectionEqmt aqySectionEqmt)
    {
        return aqySectionEqmtMapper.selectAqySectionEqmtList(aqySectionEqmt);
    }

    /**
     * 新增断面配置的监测设备类型
     *
     * @param aqySectionEqmt 断面配置的监测设备类型
     * @return 结果
     */
    @Override
    public int insertAqySectionEqmt(AqySectionEqmt aqySectionEqmt)
    {
        aqySectionEqmt.setCreateTime(DateUtils.getNowDate());
        return aqySectionEqmtMapper.insertAqySectionEqmt(aqySectionEqmt);
    }

    /**
     * 修改断面配置的监测设备类型
     *
     * @param aqySectionEqmt 断面配置的监测设备类型
     * @return 结果
     */
    @Override
    public int updateAqySectionEqmt(AqySectionEqmt aqySectionEqmt)
    {
        return aqySectionEqmtMapper.updateAqySectionEqmt(aqySectionEqmt);
    }

    /**
     * 批量删除断面配置的监测设备类型
     *
     * @param ids 需要删除的断面配置的监测设备类型主键
     * @return 结果
     */
    @Override
    public int deleteAqySectionEqmtByIds(Long[] ids)
    {
        return aqySectionEqmtMapper.deleteAqySectionEqmtByIds(ids);
    }

    /**
     * 删除断面配置的监测设备类型信息
     *
     * @param id 断面配置的监测设备类型主键
     * @return 结果
     */
    @Override
    public int deleteAqySectionEqmtById(Long id)
    {
        return aqySectionEqmtMapper.deleteAqySectionEqmtById(id);
    }

    @Override
    public List<String> listEqmtTypeByProjectId(Long projectId) {
        return aqySectionEqmtMapper.listEqmtTypeByProjectId(projectId);
    }
}
