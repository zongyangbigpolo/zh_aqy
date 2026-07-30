package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.ArrayUtil;
import com.ruoyi.common.core.domain.aqy.*;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.websocket.WebSocketUsers;
import com.ruoyi.service.IAqyAlarmPersonService;
import com.ruoyi.service.IAqyAlarmRecordService;
import com.ruoyi.service.IAqyEquipmentService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyAlarmMapper;
import com.ruoyi.service.IAqyAlarmService;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

/**
 * 报警等级Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyAlarmServiceImpl implements IAqyAlarmService {
    @Autowired
    private AqyAlarmMapper aqyAlarmMapper;
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;
    @Autowired
    private IAqyAlarmRecordService aqyAlarmRecordService;
    @Autowired
    private IAqyAlarmPersonService aqyAlarmPersonService;

    /**
     * 查询报警等级
     *
     * @param id 报警等级主键
     * @return 报警等级
     */
    @Override
    public AqyAlarm selectAqyAlarmById(Long id) {
        return aqyAlarmMapper.selectAqyAlarmById(id);
    }

    /**
     * 查询报警等级列表
     *
     * @param aqyAlarm 报警等级
     * @return 报警等级
     */
    @Override
    public List<AqyAlarm> selectAqyAlarmList(AqyAlarm aqyAlarm) {
        return aqyAlarmMapper.selectAqyAlarmList(aqyAlarm);
    }

    /**
     * 新增报警等级
     *
     * @param aqyAlarm 报警等级
     * @return 结果
     */
    @Override
    public int insertAqyAlarm(AqyAlarm aqyAlarm) {
        aqyAlarm.setCreateTime(DateUtils.getNowDate());
        return aqyAlarmMapper.insertAqyAlarm(aqyAlarm);
    }

    /**
     * 修改报警等级
     *
     * @param aqyAlarm 报警等级
     * @return 结果
     */
    @Override
    public int updateAqyAlarm(AqyAlarm aqyAlarm) {
        return aqyAlarmMapper.updateAqyAlarm(aqyAlarm);
    }

    /**
     * 批量删除报警等级
     *
     * @param ids 需要删除的报警等级主键
     * @return 结果
     */
    @Override
    public int deleteAqyAlarmByIds(Long[] ids) {
        return aqyAlarmMapper.deleteAqyAlarmByIds(ids);
    }

    /**
     * 删除报警等级信息
     *
     * @param id 报警等级主键
     * @return 结果
     */
    @Override
    public int deleteAqyAlarmById(Long id) {
        return aqyAlarmMapper.deleteAqyAlarmById(id);
    }

    /**
     * 当获取传感器监测数据后，判断是否触发报警。
     * 如果触发报警，判断报警等级，记录报警记录，根据设置的模板，发送报警信息给相关联系人。
     * 更新设备表中当前设备的报警等级，待后台管理对报警记录做处理，记录处理结果结果，便于后续追溯。
     *
     * @param devNoList
     * @return Map<eqmtId, alarmLevelId>
     */
    @Override
    public Map<Long, Long> checkWyRawDataWillAlarm(Map<Long, AqyEquipmentWyRaw> devNoList) {
        Map<Long, Long> eqmtAlarms = new HashMap<>();
        if (ArrayUtil.isNotEmpty(devNoList)) {
            Map<Long, List<AqyAlarm>> aqyAlarmLevels = aqyAlarmMapper.selectAqyAlarmList(new AqyAlarm()).stream().collect(Collectors.groupingBy(AqyAlarm::getEqmtType));
            for (Map.Entry<Long, AqyEquipmentWyRaw> devNoEntry : devNoList.entrySet()) {
                AqyEquipment aqyEquipment = aqyEquipmentService.selectAqyEquipmentById(devNoEntry.getKey());

                Tuple3<Long, BigDecimal, BigDecimal> wyAlaramLevel = getWyAlaramLevel(devNoEntry.getValue(), aqyEquipment, aqyAlarmLevels.get(aqyEquipment.getEqmtTypeId()));
                aqyEquipment.setAccumulativeChangeValueX(wyAlaramLevel.getT2());
                aqyEquipment.setAccumulativeChangeValueY(wyAlaramLevel.getT3());
                aqyEquipment.setAlarmLevel(wyAlaramLevel.getT1());
                aqyEquipmentService.updateAqyEquipment(aqyEquipment);
                if (wyAlaramLevel.getT1() != null && wyAlaramLevel.getT1() > 0) {
                    // 保存报警记录并更新设备状态信息
                    saveAlarmRecord(wyAlaramLevel.getT1(), aqyEquipment, wyAlaramLevel.getT2(), wyAlaramLevel.getT3());
                    eqmtAlarms.put(aqyEquipment.getId(), wyAlaramLevel.getT1());
                }
            }
        }
        return eqmtAlarms;
    }

    @Override
    public void checkLfRawDataWillAlarm(Map<Long, AqyEquipmentLfRaw> devNoList) {
        if (ArrayUtil.isNotEmpty(devNoList)) {
            Map<Long, List<AqyAlarm>> aqyAlarmLevels = aqyAlarmMapper.selectAqyAlarmList(new AqyAlarm()).stream().collect(Collectors.groupingBy(AqyAlarm::getEqmtType));
            for (Map.Entry<Long, AqyEquipmentLfRaw> devNoEntry : devNoList.entrySet()) {
                AqyEquipment aqyEquipment = aqyEquipmentService.selectAqyEquipmentById(devNoEntry.getKey());
                Tuple2<Long, BigDecimal> lfAlaramLevel = getLfAlaramLevel(devNoEntry.getValue(), aqyEquipment, aqyAlarmLevels.get(aqyEquipment.getEqmtTypeId()));
                aqyEquipment.setAccumulativeChangeValueX(lfAlaramLevel.getT2());
                aqyEquipment.setAlarmLevel(lfAlaramLevel.getT1());
                aqyEquipmentService.updateAqyEquipment(aqyEquipment);
                if (lfAlaramLevel.getT1() != null && lfAlaramLevel.getT1() > 0) {
                    // 保存报警记录并更新设备状态信息
                    saveAlarmRecord(lfAlaramLevel.getT1(), aqyEquipment, lfAlaramLevel.getT2());
                }
            }
        }
    }

    @Override
    public void checkQjRawDataWillAlarm(Map<Long, AqyEquipmentQjRaw> devNoList) {
        if (ArrayUtil.isNotEmpty(devNoList)) {
            Map<Long, List<AqyAlarm>> aqyAlarmLevels = aqyAlarmMapper.selectAqyAlarmList(new AqyAlarm()).stream().collect(Collectors.groupingBy(AqyAlarm::getEqmtType));
            for (Map.Entry<Long, AqyEquipmentQjRaw> devNoEntry : devNoList.entrySet()) {
                AqyEquipment aqyEquipment = aqyEquipmentService.selectAqyEquipmentById(devNoEntry.getKey());
                Tuple4<Long, BigDecimal, BigDecimal, BigDecimal> qjAlaramLevel = getQjAlaramLevel(devNoEntry.getValue(), aqyEquipment, aqyAlarmLevels.get(aqyEquipment.getEqmtTypeId()));
                aqyEquipment.setAccumulativeChangeValueX(qjAlaramLevel.getT2());
                aqyEquipment.setAccumulativeChangeValueY(qjAlaramLevel.getT3());
                aqyEquipment.setAccumulativeChangeValueH(qjAlaramLevel.getT4());
                aqyEquipment.setAlarmLevel(qjAlaramLevel.getT1());
                aqyEquipmentService.updateAqyEquipment(aqyEquipment);
                if (qjAlaramLevel.getT1() != null && qjAlaramLevel.getT1() > 0) {
                    // 保存报警记录并更新设备状态信息
                    saveAlarmRecord(qjAlaramLevel.getT1(), aqyEquipment, qjAlaramLevel.getT2(), qjAlaramLevel.getT3(), qjAlaramLevel.getT4());
                }
            }
        }
    }

    @Override
    public void checkYlRawDataWillAlarm(Map<Long, AqyEquipmentYlRaw> devNoList) {
        if (ArrayUtil.isNotEmpty(devNoList)) {
            Map<Long, List<AqyAlarm>> aqyAlarmLevels = aqyAlarmMapper.selectAqyAlarmList(new AqyAlarm()).stream().collect(Collectors.groupingBy(AqyAlarm::getEqmtType));
            for (Map.Entry<Long, AqyEquipmentYlRaw> devNoEntry : devNoList.entrySet()) {
                AqyEquipment aqyEquipment = aqyEquipmentService.selectAqyEquipmentById(devNoEntry.getKey());
                Tuple2<Long, BigDecimal> ylAlaramLevel = getYlAlaramLevel(devNoEntry.getValue(), aqyEquipment, aqyAlarmLevels.get(aqyEquipment.getEqmtTypeId()));
                aqyEquipment.setAccumulativeChangeValueX(ylAlaramLevel.getT2());
                aqyEquipment.setAlarmLevel(ylAlaramLevel.getT1());
                aqyEquipmentService.updateAqyEquipment(aqyEquipment);
                if (ylAlaramLevel.getT1() != null && ylAlaramLevel.getT1() > 0) {
                    // 保存报警记录并更新设备状态信息
                    saveAlarmRecord(ylAlaramLevel.getT1(), aqyEquipment, ylAlaramLevel.getT2());
                }
            }
        }
    }

    private Tuple3<Long, BigDecimal, BigDecimal> getWyAlaramLevel(AqyEquipmentWyRaw rawData, AqyEquipment aqyEquipment, List<AqyAlarm> alarmLevels) {
//        1. 加法（add） 2. 减法（subtract）3. 乘法（multiply）4. 除法（divide）
        Long levelId = 0L;
        BigDecimal theDiffValueX = BigDecimal.ZERO, theDiffValueY = BigDecimal.ZERO;
        if (ArrayUtil.isNotEmpty(rawData)) {
            if (rawData.getXOrY().equals("X"))
                theDiffValueX = rawData.getValueWy().subtract(aqyEquipment.getInitialX());
            else if (rawData.getXOrY().equals("Y"))
                theDiffValueY = rawData.getValueWy().subtract(aqyEquipment.getInitialX());
            else
                throw new RuntimeException("没有设置X_or_Y字段值");

            for (AqyAlarm alarmLevel : alarmLevels) {
                if (new BigDecimal(Math.abs(theDiffValueX.doubleValue())).compareTo(alarmLevel.getAccumulativeThresholdValue()) >= 0
                        || new BigDecimal(Math.abs(theDiffValueY.doubleValue())).compareTo(alarmLevel.getAccumulativeThresholdValue()) >= 0) {
                    levelId = alarmLevel.getId();
                    break;
                }
            }
        }
        return Tuples.of(levelId, theDiffValueX, theDiffValueY);
    }

    private Tuple2<Long, BigDecimal> getLfAlaramLevel(AqyEquipmentLfRaw rawData, AqyEquipment aqyEquipment, List<AqyAlarm> alarmLevels) {
        Long levelId = 0L;
        BigDecimal theDiffValueX = BigDecimal.ZERO;
        if (ArrayUtil.isNotEmpty(rawData)) {
            theDiffValueX = rawData.getLfValue().subtract(aqyEquipment.getInitialX());
            for (AqyAlarm alarmLevel : alarmLevels) {
                if (new BigDecimal(Math.abs(theDiffValueX.doubleValue())).compareTo(alarmLevel.getAccumulativeThresholdValue()) >= 0) {
                    levelId = alarmLevel.getId();
                }
            }
        }
        return Tuples.of(levelId, theDiffValueX);
    }

    private Tuple4<Long, BigDecimal, BigDecimal, BigDecimal> getQjAlaramLevel(AqyEquipmentQjRaw rawData, AqyEquipment aqyEquipment, List<AqyAlarm> alarmLevels) {
        Long levelId = 0L;
        BigDecimal theDiffValueX = BigDecimal.ZERO, theDiffValueY = BigDecimal.ZERO, theDiffValueZ = BigDecimal.ZERO;
        if (ArrayUtil.isNotEmpty(rawData)) {
            theDiffValueX = rawData.getXValueQj().subtract(aqyEquipment.getInitialX());
            theDiffValueY = rawData.getYValueQj().subtract(aqyEquipment.getInitialY());
            theDiffValueZ = rawData.getZValueQj().subtract(aqyEquipment.getInitialH());
            for (AqyAlarm alarmLevel : alarmLevels) {
                if (new BigDecimal(Math.abs(theDiffValueX.doubleValue())).compareTo(alarmLevel.getAccumulativeThresholdValue()) >= 0
                        || new BigDecimal(Math.abs(theDiffValueY.doubleValue())).compareTo(alarmLevel.getAccumulativeThresholdValue()) >= 0
                        || new BigDecimal(Math.abs(theDiffValueZ.doubleValue())).compareTo(alarmLevel.getAccumulativeThresholdValue()) >= 0) {
                    levelId = alarmLevel.getId();
                }
            }
        }
        return Tuples.of(levelId, theDiffValueX, theDiffValueY, theDiffValueZ);
    }

    private Tuple2<Long, BigDecimal> getYlAlaramLevel(AqyEquipmentYlRaw rawData, AqyEquipment aqyEquipment, List<AqyAlarm> alarmLevels) {
        Long levelId = 0L;
        BigDecimal theDiffValueX = BigDecimal.ZERO;
        if (ArrayUtil.isNotEmpty(rawData)) {
            theDiffValueX = rawData.getYlValue().subtract(aqyEquipment.getInitialX());
            for (AqyAlarm alarmLevel : alarmLevels) {
                if (new BigDecimal(Math.abs(theDiffValueX.doubleValue())).compareTo(alarmLevel.getAccumulativeThresholdValue()) >= 0) {
                    levelId = alarmLevel.getId();
                }
            }
        }
        return Tuples.of(levelId, theDiffValueX);
    }

    private void saveAlarmRecord(Long levelId, AqyEquipment aqyEquipment, BigDecimal... diffValues) {
        try {
            AqyAlarm aqyAlarm = aqyAlarmMapper.selectAqyAlarmById(levelId);
            AqyAlarmRecord aqyAlarmRecord = new AqyAlarmRecord();
            aqyAlarmRecord.setProjectId(aqyEquipment.getProjectId());
            aqyAlarmRecord.setQmtId(aqyEquipment.getId());
            aqyAlarmRecord.setEqmtName(aqyEquipment.getEqmtName());
            aqyAlarmRecord.setAlarmId(levelId);
            aqyAlarmRecord.setAlarmLevel(aqyAlarm.getAlarmLevel());
            aqyAlarmRecord.setAccumulativeValue1(diffValues != null && diffValues.length > 0 ? diffValues[0] : null);
            aqyAlarmRecord.setAccumulativeValue2(diffValues != null && diffValues.length > 1 ? diffValues[1] : null);
            aqyAlarmRecord.setAccumulativeValue3(diffValues != null && diffValues.length > 2 ? diffValues[2] : null);
            aqyAlarmRecord.setAlarmColor(aqyAlarm.getAlarmColor());
            aqyAlarmRecord.setAlarmContent(aqyAlarm.getAlarmTemplate());
            aqyAlarmRecord.setRecordTime(DateUtils.getNowDate());
            aqyAlarmRecordService.insertAqyAlarmRecord(aqyAlarmRecord);

            // 发送报警信息给相关人员
            sendAlarmMessageToPerson(aqyAlarm.getAlarmLevel());
        } catch (Exception ex) {

        }
    }

    private void sendAlarmMessageToPerson(Integer alarmLevel) {
        AqyAlarmPerson query = new AqyAlarmPerson();
        query.setAlarmLevel(alarmLevel);
        List<AqyAlarmPerson> aqyAlarmPeople = aqyAlarmPersonService.selectAqyAlarmPersonList(query);
        // 调用短信接口，发送短信
        for (AqyAlarmPerson aqyAlarmPerson : aqyAlarmPeople) {
            
        }
    }
}
