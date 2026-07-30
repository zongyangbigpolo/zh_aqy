package com.ruoyi.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.aqy.AqyAlarmRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyAlarmRecordMapper;
import com.ruoyi.service.IAqyAlarmRecordService;

/**
 * 报警记录Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyAlarmRecordServiceImpl implements IAqyAlarmRecordService
{
    @Autowired
    private AqyAlarmRecordMapper aqyAlarmRecordMapper;

    /**
     * 查询报警记录
     *
     * @param id 报警记录主键
     * @return 报警记录
     */
    @Override
    public AqyAlarmRecord selectAqyAlarmRecordById(Long id)
    {
        return aqyAlarmRecordMapper.selectAqyAlarmRecordById(id);
    }

    /**
     * 查询报警记录列表
     *
     * @param aqyAlarmRecord 报警记录
     * @return 报警记录
     */
    @Override
    public List<AqyAlarmRecord> selectAqyAlarmRecordList(AqyAlarmRecord aqyAlarmRecord)
    {
        return aqyAlarmRecordMapper.selectAqyAlarmRecordList(aqyAlarmRecord);
    }

    /**
     * 新增报警记录
     *
     * @param aqyAlarmRecord 报警记录
     * @return 结果
     */
    @Override
    public int insertAqyAlarmRecord(AqyAlarmRecord aqyAlarmRecord)
    {
        return aqyAlarmRecordMapper.insertAqyAlarmRecord(aqyAlarmRecord);
    }

    /**
     * 修改报警记录
     *
     * @param aqyAlarmRecord 报警记录
     * @return 结果
     */
    @Override
    public int updateAqyAlarmRecord(AqyAlarmRecord aqyAlarmRecord)
    {
        return aqyAlarmRecordMapper.updateAqyAlarmRecord(aqyAlarmRecord);
    }

    /**
     * 批量删除报警记录
     *
     * @param ids 需要删除的报警记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyAlarmRecordByIds(Long[] ids)
    {
        return aqyAlarmRecordMapper.deleteAqyAlarmRecordByIds(ids);
    }

    /**
     * 删除报警记录信息
     *
     * @param id 报警记录主键
     * @return 结果
     */
    @Override
    public int deleteAqyAlarmRecordById(Long id)
    {
        return aqyAlarmRecordMapper.deleteAqyAlarmRecordById(id);
    }
}
