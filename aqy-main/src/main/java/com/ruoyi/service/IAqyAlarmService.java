package com.ruoyi.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.ruoyi.common.core.domain.aqy.*;

import java.util.List;
import java.util.Map;

/**
 * 报警等级Service接口
 *
 * @author MXJ
 * @date 2024-10-13
 */
public interface IAqyAlarmService {
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
     * 批量删除报警等级
     *
     * @param ids 需要删除的报警等级主键集合
     * @return 结果
     */
    public int deleteAqyAlarmByIds(Long[] ids);

    /**
     * 删除报警等级信息
     *
     * @param id 报警等级主键
     * @return 结果
     */
    public int deleteAqyAlarmById(Long id);

    /**
     * 当获取传感器监测数据后，判断是否触发报警。
     * 如果触发报警，判断报警等级，记录报警记录，根据设置的模板，发送报警信息给相关联系人。
     * 更新设备表中当前设备的报警等级，待后台管理对报警记录做处理，记录处理结果结果，便于后续追溯。
     * @param devNoList
     */
    public Map<Long, Long> checkWyRawDataWillAlarm(Map<Long, AqyEquipmentWyRaw> devNoList);

    public void checkLfRawDataWillAlarm(Map<Long, AqyEquipmentLfRaw> devNoList);

    public void checkQjRawDataWillAlarm(Map<Long, AqyEquipmentQjRaw> devNoList);

    public void checkYlRawDataWillAlarm(Map<Long, AqyEquipmentYlRaw> devNoList);
}
