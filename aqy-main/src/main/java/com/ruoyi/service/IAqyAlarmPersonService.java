package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqyAlarmPerson;

import java.util.List;

/**
 * 报警联系人Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyAlarmPersonService
{
    /**
     * 查询报警联系人
     *
     * @param id 报警联系人主键
     * @return 报警联系人
     */
    public AqyAlarmPerson selectAqyAlarmPersonById(Long id);

    /**
     * 查询报警联系人列表
     *
     * @param aqyAlarmPerson 报警联系人
     * @return 报警联系人集合
     */
    public List<AqyAlarmPerson> selectAqyAlarmPersonList(AqyAlarmPerson aqyAlarmPerson);

    /**
     * 新增报警联系人
     *
     * @param aqyAlarmPerson 报警联系人
     * @return 结果
     */
    public int insertAqyAlarmPerson(AqyAlarmPerson aqyAlarmPerson);

    /**
     * 修改报警联系人
     *
     * @param aqyAlarmPerson 报警联系人
     * @return 结果
     */
    public int updateAqyAlarmPerson(AqyAlarmPerson aqyAlarmPerson);

    /**
     * 批量删除报警联系人
     *
     * @param ids 需要删除的报警联系人主键集合
     * @return 结果
     */
    public int deleteAqyAlarmPersonByIds(Long[] ids);

    /**
     * 删除报警联系人信息
     *
     * @param id 报警联系人主键
     * @return 结果
     */
    public int deleteAqyAlarmPersonById(Long id);
}
