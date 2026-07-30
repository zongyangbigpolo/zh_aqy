package com.ruoyi.mapper;

import com.ruoyi.common.core.domain.aqy.AqyProject;

import java.util.List;


/**
 * 工程项目Mapper接口
 * 
 * @author MXJ
 * @date 2024-09-11
 */
public interface AqyProjectMapper 
{
    /**
     * 查询工程项目
     * 
     * @param id 工程项目主键
     * @return 工程项目
     */
    public AqyProject selectAqyProjectById(Long id);

    /**
     * 查询工程项目列表
     * 
     * @param aqyProject 工程项目
     * @return 工程项目集合
     */
    public List<AqyProject> selectAqyProjectList(AqyProject aqyProject);

    /**
     * 新增工程项目
     * 
     * @param aqyProject 工程项目
     * @return 结果
     */
    public int insertAqyProject(AqyProject aqyProject);

    /**
     * 修改工程项目
     * 
     * @param aqyProject 工程项目
     * @return 结果
     */
    public int updateAqyProject(AqyProject aqyProject);

    /**
     * 删除工程项目
     * 
     * @param id 工程项目主键
     * @return 结果
     */
    public int deleteAqyProjectById(Long id);

    /**
     * 批量删除工程项目
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyProjectByIds(Long[] ids);
}
