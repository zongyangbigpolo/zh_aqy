package com.ruoyi.web.controller.mqtt;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.aqy.*;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.mqtt.enums.CommandMessageType;
import com.ruoyi.mqtt.model.CmdSampleParamDev;
import com.ruoyi.mqtt.model.CmdSetThresholdParamDev;
import com.ruoyi.mqtt.model.IotData;
import com.ruoyi.mqtt.sender.CmdMqttSender;
import com.ruoyi.mqtt.sender.IMqttSender;
import com.ruoyi.service.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mqttCmd")
@Api(value = "MQTT接口", tags = "MQTT接口")
public class MqttController {
    @Resource
    private CmdMqttSender cmdMqttSender;
    @Autowired
    private IAqyGatwayEquipmentService gatwayEquipmentService;
    @Autowired
    private IAqyEquipmentService equipmentService;
    @Autowired
    private IAqyEquipmentTypeService equipmentTypeService;
    @Autowired
    private IAqyAlarmService aqyAlarmService;
    @Autowired
    private IAqyAlarmEquipmentService aqyAlarmEquipmentService;

    @PostMapping("/cmd/downConfig")
    public AjaxResult downConfigOne(@RequestBody AqyGatwayEquipment gatwayEquipment) {
        downConfig(gatwayEquipment.getId());
        return AjaxResult.success();
    }

    @PostMapping("/cmd/batchDownConfig")
    public AjaxResult downConfigMulti(@RequestBody Long[] gatwayIds) {
        if (gatwayIds != null) {
            Arrays.stream(gatwayIds).forEach(this::downConfig);
        }
        return AjaxResult.success();
    }

    @PostMapping("/cmd/setSampleParam")
    public AjaxResult setSampleParamOne(@RequestBody AqyGatwayEquipment gatwayEquipment) {
        setSampleParam(gatwayEquipment.getId());
        return AjaxResult.success();
    }

    @PostMapping("/cmd/batchSetSampleParam")
    public AjaxResult setSampleParamMulti(@RequestBody Long[] gatwayIds) {
        if (gatwayIds != null) {
            Arrays.stream(gatwayIds).forEach(this::setSampleParam);
        }
        return AjaxResult.success();
    }

    @PostMapping("/cmd/setThresholdValue")
    public AjaxResult setThresholdValueOne(@RequestBody AqyGatwayEquipment gatwayEquipment) {
        setThresholdValue(gatwayEquipment.getId());
        return AjaxResult.success();
    }

    @PostMapping("/cmd/batchSetThresholdValue")
    public AjaxResult setThresholdValueMulti(@RequestBody Long[] gatwayIds) {
        if (gatwayIds != null) {
            Arrays.stream(gatwayIds).forEach(this::setThresholdValue);
        }
        return AjaxResult.success();
    }

    @PostMapping("/cmd/setWorkMode")
    public AjaxResult setWorkMode(@RequestBody AqyGatwayEquipment gatwayEquipment) {
        setWorkMode(gatwayEquipment.getId(), gatwayEquipment.getModel());
        return AjaxResult.success();
    }

    @PostMapping("/cmd/getSample")
    public AjaxResult getSample(@RequestBody AqyGatwayEquipment gatwayEquipment) {
        getSample(gatwayEquipment.getId());
        return AjaxResult.success();
    }

    @PostMapping("/cmd/setCurTime")
    public AjaxResult setCurTime(@RequestBody AqyGatwayEquipment gatwayEquipment) {
        setCurTime(gatwayEquipment.getId());
        return AjaxResult.success();
    }

    //报警接口
    @PostMapping("/cmd/soundLightAlarm")
    public AjaxResult soundLightAlarm() {
        List<AqyAlarmEquipment> aqyAlarmEquipments = aqyAlarmEquipmentService.selectAqyAlarmEquipmentList(null);
        for (AqyAlarmEquipment aqyAlarmEquipment : aqyAlarmEquipments) {
            soundLightAlarm(aqyAlarmEquipment.getEqmtCode(), aqyAlarmEquipment.getAlarmTime());
        }
        return AjaxResult.success();
    }

    @PostMapping("/cmd/reboot")
    public AjaxResult reboot(@RequestBody AqyGatwayEquipment gatwayEquipment) {
        reboot(gatwayEquipment.getId());
        return AjaxResult.success();
    }

    @PostMapping("/cmd/getStatus")
    public AjaxResult getStatus(@RequestBody AqyGatwayEquipment gatwayEquipment) {
        getStatus(gatwayEquipment.getId());
        return AjaxResult.success();
    }

    @PostMapping("/cmd/catchImg")
    public AjaxResult catchImg(@RequestBody AqyEquipment aqyEquipment) {
        getImg(aqyEquipment);
        return AjaxResult.success();
    }

    private void downConfig(Long gatwayId) {
        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
        if (aqyGatwayEquipment != null) {
            AqyEquipment query = new AqyEquipment();
            query.setGatwayId(gatwayId);
            List<AqyEquipment> aqyEquipments = equipmentService.selectAqyEquipmentList(query);
            if (CollectionUtil.isNotEmpty(aqyEquipments)) {
                List<String> aqyEquipmentCodes = aqyEquipments.stream().map(AqyEquipment::getEqmtCode).collect(Collectors.toList());
                IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.DOWN_CONFIG.getKeyCode());

                cmdMqttSender.publisDownConfig(iotData, aqyEquipmentCodes.toArray(new String[0]));
            }
        }
    }

    private void setSampleParam(Long gatwayId) {
        Map<Long, AqyEquipmentType> aqyEquipmentMap = getEquipmentTypes();
        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
        if (aqyGatwayEquipment != null) {
            AqyEquipment query = new AqyEquipment();
            query.setGatwayId(gatwayId);
            List<AqyEquipment> aqyEquipments = equipmentService.selectAqyEquipmentList(query);
            if (CollectionUtil.isNotEmpty(aqyEquipments)) {
                List<CmdSampleParamDev> collect = aqyEquipments.stream().map(item -> getSendSampleParam(item, aqyEquipmentMap.get(item.getEqmtTypeId()))).collect(Collectors.toList());
                IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.SET_SAMPLE_PARAM.getKeyCode());
                cmdMqttSender.publishSampleParam(iotData, collect.toArray(new CmdSampleParamDev[0]));
            }
        }
    }

    private void setThresholdValue(Long gatwayId) {
        Map<Long, List<AqyAlarm>> alarmLevels = getAlarmLevels();
        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
        if (aqyGatwayEquipment != null) {
            AqyEquipment query = new AqyEquipment();
            query.setGatwayId(gatwayId);
            List<AqyEquipment> aqyEquipments = equipmentService.selectAqyEquipmentList(query);
            if (CollectionUtil.isNotEmpty(aqyEquipments)) {
                CmdSetThresholdParamDev thresholdParamDev = aqyEquipments.stream().map(item -> getSendThresholdParam(item, alarmLevels.get(item.getEqmtTypeId()))).findFirst().get();
                IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.SET_THRESHOLD_PARAM.getKeyCode());
                cmdMqttSender.publishThresholdParam(iotData, thresholdParamDev);
            }
        }
    }

    private void setWorkMode(Long gatwayId, Integer model) {
        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
        if (aqyGatwayEquipment != null) {
            IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.SET_WORK_MODE.getKeyCode());
            cmdMqttSender.publishSetWorkMode(iotData, model);
        }
    }

    private void getSample(Long gatwayId) {
        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
        if (aqyGatwayEquipment != null) {
            IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.SAMPLE.getKeyCode());
            cmdMqttSender.publishSample(iotData);
        }
    }

    private void setCurTime(Long gatwayId) {
        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
        if (aqyGatwayEquipment != null) {
            IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.SET_TIME.getKeyCode());
            cmdMqttSender.publishSetTime(iotData, DateUtils.getTime());
        }
    }

    //    private void soundLightAlarm(Long gatwayId, Integer duration) {
//        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
//        if (aqyGatwayEquipment != null) {
//            IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.SOUND_LIGHT_ALARM.getKeyCode());
//            cmdMqttSender.publishSoundLightAlarm(iotData, duration);
//        }
//    }
    private void soundLightAlarm(String eqmtCode, Long duration) {
        IotData iotData = new IotData(eqmtCode, CommandMessageType.SOUND_LIGHT_ALARM.getKeyCode());
        cmdMqttSender.publishSoundLightAlarm(iotData, duration);
    }

    private void reboot(Long gatwayId) {
        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
        if (aqyGatwayEquipment != null) {
            IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.REBOOT.getKeyCode());
            cmdMqttSender.publishReboot(iotData);
        }
    }

    private void getStatus(Long gatwayId) {
        AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentById(gatwayId);
        if (aqyGatwayEquipment != null) {
            IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.GET_STATUS.getKeyCode());
            cmdMqttSender.publishGetStatus(iotData);
        }
    }

    private void getImg(AqyEquipment aqyEquipment) {
        if (aqyEquipment != null) {
            AqyGatwayEquipment aqyGatwayEquipment = gatwayEquipmentService.selectAqyGatwayEquipmentByGatwayId(aqyEquipment.getGatwayId());
            IotData iotData = new IotData(aqyGatwayEquipment.getGatwayCode(), CommandMessageType.GET_IMG.getKeyCode());
            cmdMqttSender.publishGetImage(iotData, aqyEquipment.getEqmtCode());
        }
    }

    private Map<Long, AqyEquipmentType> getEquipmentTypes() {
        List<AqyEquipmentType> aqyActFrequencies = equipmentTypeService.selectAqyEquipmentTypeList(new AqyEquipmentType());
        Map<Long, AqyEquipmentType> collect = aqyActFrequencies.stream().collect(Collectors.toMap(AqyEquipmentType::getId, AqyEquipmentType -> AqyEquipmentType));
        return collect;
    }

    private Map<Long, List<AqyAlarm>> getAlarmLevels() {
        List<AqyAlarm> aqyAlarmLevels = aqyAlarmService.selectAqyAlarmList(new AqyAlarm());
        Map<Long, List<AqyAlarm>> collect = aqyAlarmLevels.stream().collect(Collectors.groupingBy(AqyAlarm::getEqmtType));
        return collect;
    }

    private CmdSampleParamDev getSendSampleParam(AqyEquipment aqyEquipment, AqyEquipmentType aqyEquipmentType) {
        CmdSampleParamDev sampleParamDev = new CmdSampleParamDev();
        sampleParamDev.setDevNo(aqyEquipment.getEqmtCode());
        sampleParamDev.setUploadIntv(aqyEquipmentType.getUploadIntv());
        sampleParamDev.setUploadIntv(aqyEquipmentType.getPlusIntv());
        sampleParamDev.setSamplingFrequency(1);
        return sampleParamDev;
    }

    private CmdSetThresholdParamDev getSendThresholdParam(AqyEquipment aqyEquipment, List<AqyAlarm> alarmLevels) {
        BigDecimal[] thresholds = new BigDecimal[alarmLevels.size()];
        BigDecimal[] upperLimits = new BigDecimal[alarmLevels.size()];
        BigDecimal[] lowerLimits = new BigDecimal[alarmLevels.size()];
        for (int it = 0, itCount = alarmLevels.size(); it < itCount; it++) {
            thresholds[it] = alarmLevels.get(it).getAccumulativeThresholdValue();
            upperLimits[it] = alarmLevels.get(it).getUpperLimit();
            lowerLimits[it] = alarmLevels.get(it).getLowerLimit();
        }
        CmdSetThresholdParamDev cmdSetThresholdParamDev = new CmdSetThresholdParamDev(aqyEquipment.getEqmtCode(), thresholds, upperLimits, lowerLimits);
        return cmdSetThresholdParamDev;
    }
}
