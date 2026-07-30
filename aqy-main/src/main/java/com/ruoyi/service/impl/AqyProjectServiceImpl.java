package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyProject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.mapper.AqyProjectMapper;
import com.ruoyi.service.IAqyProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 工程项目Service业务层处理
 * 
 * @author MXJ
 * @date 2024-09-11
 */
@Service
public class AqyProjectServiceImpl implements IAqyProjectService
{
    @Autowired
    private AqyProjectMapper aqyProjectMapper;

    /**
     * 查询工程项目
     * 
     * @param id 工程项目主键
     * @return 工程项目
     */
    @Override
    public AqyProject selectAqyProjectById(Long id)
    {
        return aqyProjectMapper.selectAqyProjectById(id);
    }

    /**
     * 查询工程项目列表
     * 
     * @param aqyProject 工程项目
     * @return 工程项目
     */
    @Override
    public List<AqyProject> selectAqyProjectList(AqyProject aqyProject)
    {
        return aqyProjectMapper.selectAqyProjectList(aqyProject);
    }

    /**
     * 新增工程项目
     * 
     * @param aqyProject 工程项目
     * @return 结果
     */
    @Override
    public int insertAqyProject(AqyProject aqyProject)
    {
        aqyProject.setCreateTime(DateUtils.getNowDate());
        return aqyProjectMapper.insertAqyProject(aqyProject);
    }

    /**
     * 修改工程项目
     * 
     * @param aqyProject 工程项目
     * @return 结果
     */
    @Override
    public int updateAqyProject(AqyProject aqyProject)
    {
        return aqyProjectMapper.updateAqyProject(aqyProject);
    }

    /**
     * 批量删除工程项目
     * 
     * @param ids 需要删除的工程项目主键
     * @return 结果
     */
    @Override
    public int deleteAqyProjectByIds(Long[] ids)
    {
        return aqyProjectMapper.deleteAqyProjectByIds(ids);
    }

    /**
     * 删除工程项目信息
     * 
     * @param id 工程项目主键
     * @return 结果
     */
    @Override
    public int deleteAqyProjectById(Long id)
    {
        return aqyProjectMapper.deleteAqyProjectById(id);
    }
}
