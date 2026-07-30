package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyAlarmPerson;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyAlarmPersonMapper;
import com.ruoyi.service.IAqyAlarmPersonService;

/**
 * 报警联系人Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyAlarmPersonServiceImpl implements IAqyAlarmPersonService
{
    @Autowired
    private AqyAlarmPersonMapper aqyAlarmPersonMapper;

    /**
     * 查询报警联系人
     *
     * @param id 报警联系人主键
     * @return 报警联系人
     */
    @Override
    public AqyAlarmPerson selectAqyAlarmPersonById(Long id)
    {
        return aqyAlarmPersonMapper.selectAqyAlarmPersonById(id);
    }

    /**
     * 查询报警联系人列表
     *
     * @param aqyAlarmPerson 报警联系人
     * @return 报警联系人
     */
    @Override
    public List<AqyAlarmPerson> selectAqyAlarmPersonList(AqyAlarmPerson aqyAlarmPerson)
    {
        return aqyAlarmPersonMapper.selectAqyAlarmPersonList(aqyAlarmPerson);
    }

    /**
     * 新增报警联系人
     *
     * @param aqyAlarmPerson 报警联系人
     * @return 结果
     */
    @Override
    public int insertAqyAlarmPerson(AqyAlarmPerson aqyAlarmPerson)
    {
        aqyAlarmPerson.setCreateTime(DateUtils.getNowDate());
        return aqyAlarmPersonMapper.insertAqyAlarmPerson(aqyAlarmPerson);
    }

    /**
     * 修改报警联系人
     *
     * @param aqyAlarmPerson 报警联系人
     * @return 结果
     */
    @Override
    public int updateAqyAlarmPerson(AqyAlarmPerson aqyAlarmPerson)
    {
        return aqyAlarmPersonMapper.updateAqyAlarmPerson(aqyAlarmPerson);
    }

    /**
     * 批量删除报警联系人
     *
     * @param ids 需要删除的报警联系人主键
     * @return 结果
     */
    @Override
    public int deleteAqyAlarmPersonByIds(Long[] ids)
    {
        return aqyAlarmPersonMapper.deleteAqyAlarmPersonByIds(ids);
    }

    /**
     * 删除报警联系人信息
     *
     * @param id 报警联系人主键
     * @return 结果
     */
    @Override
    public int deleteAqyAlarmPersonById(Long id)
    {
        return aqyAlarmPersonMapper.deleteAqyAlarmPersonById(id);
    }
}
