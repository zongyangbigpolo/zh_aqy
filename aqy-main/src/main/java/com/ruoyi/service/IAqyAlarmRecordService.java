package com.ruoyi.service;

import com.ruoyi.common.core.domain.aqy.AqyAlarmRecord;

import java.util.List;

/**
 * 报警记录Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyAlarmRecordService
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
     * 批量删除报警记录
     *
     * @param ids 需要删除的报警记录主键集合
     * @return 结果
     */
    public int deleteAqyAlarmRecordByIds(Long[] ids);

    /**
     * 删除报警记录信息
     *
     * @param id 报警记录主键
     * @return 结果
     */
    public int deleteAqyAlarmRecordById(Long id);
}
