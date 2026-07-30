package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyAlarm;

/**
 * 报警等级Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyAlarmMapper
{
    /**
     * 查询报警等级
     *
     * @param id 报警等级主键
     * @return 报警等级
     */
    public AqyAlarm selectAqyAlarmById(Long id);

    /**
     * 查询报警等级列表
     *
     * @param aqyAlarm 报警等级
     * @return 报警等级集合
     */
    public List<AqyAlarm> selectAqyAlarmList(AqyAlarm aqyAlarm);

    /**
     * 新增报警等级
     *
     * @param aqyAlarm 报警等级
     * @return 结果
     */
    public int insertAqyAlarm(AqyAlarm aqyAlarm);

    /**
     * 修改报警等级
     *
     * @param aqyAlarm 报警等级
     * @return 结果
     */
    public int updateAqyAlarm(AqyAlarm aqyAlarm);

    /**
     * 删除报警等级
     *
     * @param id 报警等级主键
     * @return 结果
     */
    public int deleteAqyAlarmById(Long id);

    /**
     * 批量删除报警等级
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyAlarmByIds(Long[] ids);
}
