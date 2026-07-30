package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.aqy.AqyAlarmRecord;

/**
 * 报警记录Mapper接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface AqyAlarmRecordMapper
{
    /**
     * 查询报警记录
     *
     * @param id 报警记录主键
     * @return 报警记录
     */
    public AqyAlarmRecord selectAqyAlarmRecordById(Long id);

    /**
     * 查询报警记录列表
     *
     * @param aqyAlarmRecord 报警记录
     * @return 报警记录集合
     */
    public List<AqyAlarmRecord> selectAqyAlarmRecordList(AqyAlarmRecord aqyAlarmRecord);

    /**
     * 新增报警记录
     *
     * @param aqyAlarmRecord 报警记录
     * @return 结果
     */
    public int insertAqyAlarmRecord(AqyAlarmRecord aqyAlarmRecord);

    /**
     * 修改报警记录
     *
     * @param aqyAlarmRecord 报警记录
     * @return 结果
     */
    public int updateAqyAlarmRecord(AqyAlarmRecord aqyAlarmRecord);

    /**
     * 删除报警记录
     *
     * @param id 报警记录主键
     * @return 结果
     */
    public int deleteAqyAlarmRecordById(Long id);

    /**
     * 批量删除报警记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAqyAlarmRecordByIds(Long[] ids);
}
