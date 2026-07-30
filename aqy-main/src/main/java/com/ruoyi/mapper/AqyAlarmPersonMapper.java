package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyAlarmPerson;

/**
 * 报警联系人Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyAlarmPersonMapper
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
     * 删除报警联系人
     *
     * @param id 报警联系人主键
     * @return 结果
     */
    public int deleteAqyAlarmPersonById(Long id);

    /**
     * 批量删除报警联系人
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyAlarmPersonByIds(Long[] ids);
}
