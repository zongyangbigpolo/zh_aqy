package com.ruoyi.mqtt.strategy;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.aqy.*;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.websocket.WebSocketUsers;
import com.ruoyi.service.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 智能网关上传监测数据
 * @Author：MXJ
 * @Date：2024/10/11 16:41
 */
@Slf4j
@Component
public class RawDataStrategy implements MessageHandlerStrategy {
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;
    @Autowired
    private IAqyEquipmentWyRawService aqyEquipmentWyRawService;
    @Autowired
    private IAqyEquipmentLfRawService aqyEquipmentLfRawService;
    @Autowired
    private IAqyEquipmentQjRawService aqyEquipmentQjRawService;
    @Autowired
    private IAqyEquipmentYlRawService aqyEquipmentYlRawService;
    @Autowired
    private IAqyAlarmService aqyAlarmService;
    @Autowired
    private IAqyEquipmentTypeService aqyEquipmentTypeService;

    @Override
    public void handle(String payload) {
        try {
            log.info("RawDataStrategy收到消息:" + payload);
            Map<String, Map<String, Map<String,String>>> bean = JSONObject.parseObject(payload, Map.class);
            if (bean != null) {
//                Map<Long, AqyEquipmentWyRaw> wyMap = new HashMap<>();
                Map<Long, AqyEquipmentLfRaw> lfMap = new HashMap<>();
                Map<Long, AqyEquipmentQjRaw> qjMap = new HashMap<>();
                Map<Long, AqyEquipmentYlRaw> ylMap = new HashMap<>();
                for (Map.Entry<String, Map<String,Map<String, String>>> dataEntry : bean.entrySet()) {
                    if(dataEntry.getKey().equalsIgnoreCase("msgId"))
                        break;

                    Map<String,Map<String, String>> dataMaps = dataEntry.getValue();
                    for (Map.Entry<String, Map<String, String>> devEntry : dataMaps.entrySet()) {
                        List<AqyEquipment> aqyEquipmentList = aqyEquipmentService.selectAqyEquipmentByCode(devEntry.getKey());
//                        List<AqyEquipment> aqyEquipmentList = aqyEquipmentService.selectAqyEquipmentByCode(dataEntry.getKey().equals("data")?devEntry.getKey():dataEntry.getKey());
                        if(aqyEquipmentList != null && aqyEquipmentList.size() > 0) {
                            AqyEquipmentType aqyEquipmentType = aqyEquipmentTypeService.selectAqyEquipmentTypeById(aqyEquipmentList.get(0).getEqmtTypeId());
                            switch (aqyEquipmentType.getEqmtTypeSymbol()) {
                                case "WY":
//                                    List<AqyEquipmentWyRaw> wyTimeDataList = new ArrayList<>();
//                                    AqyEquipmentWyRaw aqyEquipmentWyRaw;
//                                    for (Map.Entry<String, String> timeEntry :devEntry.getValue().entrySet()) {
//                                        String[] splitValues = timeEntry.getValue().split(",");
//                                        if (splitValues.length > 1 && splitValues.length % 2 == 0) {
//                                            for(int it = 0, itCount = aqyEquipmentList.size(); it < itCount; it++){
//                                                if(splitValues.length > it * 2 + 1) {
//                                                    aqyEquipmentWyRaw = new AqyEquipmentWyRaw();
//                                                    aqyEquipmentWyRaw.setEqmtId(aqyEquipmentList.get(it).getId());
//                                                    aqyEquipmentWyRaw.setEqmtName(aqyEquipmentList.get(it).getEqmtName());
//                                                    aqyEquipmentWyRaw.setEqmtCode(aqyEquipmentList.get(it).getEqmtCode());
//                                                    aqyEquipmentWyRaw.setCatchTime(Long.parseLong(timeEntry.getKey()));
//                                                    aqyEquipmentWyRaw.setXValueWy(new BigDecimal(splitValues[it * 2]));
//                                                    aqyEquipmentWyRaw.setYValueWy(new BigDecimal(splitValues[it * 2 + 1]));
//                                                    aqyEquipmentWyRaw.setCreateTime(DateUtils.getNowDate());
//                                                    aqyEquipmentWyRawService.insertAqyEquipmentWyRaw(aqyEquipmentWyRaw);
//                                                    wyMap.put(aqyEquipmentList.get(it).getId(), aqyEquipmentWyRaw);
//                                                }
//                                            }
//                                        }
//                                    }
//                                    // 调用是否报警服务
//                                    aqyAlarmService.checkWyRawDataWillAlarm(wyMap);
//                                    // 更新前端数据
//                                    WebSocketUsers.pushMessage(2, -1, null);
                                    break;
                                case "LF":
                                    List<AqyEquipmentLfRaw> lfTimeDataList = new ArrayList<>();
                                    AqyEquipmentLfRaw aqyEquipmentLfRaw;
                                    for (Map.Entry<String, String> timeEntry : devEntry.getValue().entrySet()) {
                                        String[] splitValues = timeEntry.getValue().split(",");
                                        if (splitValues.length > 0 && splitValues.length % 2 == 0) {
                                            for (int it = 0, itCount = aqyEquipmentList.size(); it < itCount; it++) {
                                                if(splitValues.length > it * 2 + 1) {
                                                    aqyEquipmentLfRaw = new AqyEquipmentLfRaw();
                                                    aqyEquipmentLfRaw.setEqmtId(aqyEquipmentList.get(it).getId());
                                                    aqyEquipmentLfRaw.setEqmtName(aqyEquipmentList.get(it).getEqmtName());
                                                    aqyEquipmentLfRaw.setEqmtCode(aqyEquipmentList.get(it).getEqmtCode());
                                                    aqyEquipmentLfRaw.setCatchTime(Long.parseLong(timeEntry.getKey()));
                                                    aqyEquipmentLfRaw.setLfValue(new BigDecimal(splitValues[it * 2]));
                                                    aqyEquipmentLfRaw.setTempValue(new BigDecimal(splitValues[it * 2 + 1]));
                                                    aqyEquipmentLfRaw.setCreateTime(DateUtils.getNowDate());
                                                    aqyEquipmentLfRawService.insertAqyEquipmentLfRaw(aqyEquipmentLfRaw);
                                                    lfMap.put(aqyEquipmentList.get(it).getId(), aqyEquipmentLfRaw);
                                                }
                                            }
                                        }
                                    }
                                    aqyAlarmService.checkLfRawDataWillAlarm(lfMap);
                                    // 更新前端数据
                                    WebSocketUsers.pushMessage(3, -1, null);
                                    break;
                                case "QJ":
                                    List<AqyEquipmentQjRaw> qjTimeDataList = new ArrayList<>();
                                    AqyEquipmentQjRaw aqyEquipmentQjRaw;
                                    for (Map.Entry<String, String> timeEntry : devEntry.getValue().entrySet()) {
                                        String[] splitValues = timeEntry.getValue().split(",");
                                        if (splitValues.length > 0 && splitValues.length % 4 == 0) {
                                            for (int it = 0, itCount = aqyEquipmentList.size(); it < itCount; it++) {
                                                if(splitValues.length > it * 4 + 3) {
                                                    aqyEquipmentQjRaw = new AqyEquipmentQjRaw();
                                                    aqyEquipmentQjRaw.setEqmtId(aqyEquipmentList.get(it).getId());
                                                    aqyEquipmentQjRaw.setEqmtName(aqyEquipmentList.get(it).getEqmtName());
                                                    aqyEquipmentQjRaw.setEqmtCode(aqyEquipmentList.get(it).getEqmtCode());
                                                    aqyEquipmentQjRaw.setCatchTime(Long.parseLong(timeEntry.getKey()));
                                                    aqyEquipmentQjRaw.setXValueQj(new BigDecimal(splitValues[it * 4]));
                                                    aqyEquipmentQjRaw.setYValueQj(new BigDecimal(splitValues[it * 4 + 1]));
                                                    aqyEquipmentQjRaw.setZValueQj(new BigDecimal(splitValues[it * 4 + 2]));
                                                    aqyEquipmentQjRaw.setTempValue(new BigDecimal(splitValues[it * 4 + 3]));
                                                    aqyEquipmentQjRaw.setCreateTime(DateUtils.getNowDate());
                                                    aqyEquipmentQjRawService.insertAqyEquipmentQjRaw(aqyEquipmentQjRaw);
                                                    qjMap.put(aqyEquipmentList.get(it).getId(), aqyEquipmentQjRaw);
                                                }
                                            }
                                        }
                                    }
                                    aqyAlarmService.checkQjRawDataWillAlarm(qjMap);
                                    // 更新前端数据
                                    WebSocketUsers.pushMessage(4, -1, null);
                                    break;
                                case "YL":
                                    List<AqyEquipmentYlRaw> ylTimeDataList = new ArrayList<>();
                                    AqyEquipmentYlRaw aqyEquipmentYlRaw;
                                    for (Map.Entry<String, String> timeEntry : devEntry.getValue().entrySet()) {
                                        String[] splitValues = timeEntry.getValue().split(",");
                                        if (splitValues.length > 0) {
                                            for (int it = 0, itCount = aqyEquipmentList.size(); it < itCount; it++) {
                                                if(splitValues.length > it) {
                                                    aqyEquipmentYlRaw = new AqyEquipmentYlRaw();
                                                    aqyEquipmentYlRaw.setEqmtId(aqyEquipmentList.get(it).getId());
                                                    aqyEquipmentYlRaw.setEqmtName(aqyEquipmentList.get(it).getEqmtName());
                                                    aqyEquipmentYlRaw.setEqmtCode(aqyEquipmentList.get(it).getEqmtCode());
                                                    aqyEquipmentYlRaw.setCatchTime(Long.parseLong(timeEntry.getKey()));
                                                    aqyEquipmentYlRaw.setYlValue(new BigDecimal(splitValues[it]));
                                                    aqyEquipmentYlRaw.setCreateTime(DateUtils.getNowDate());
                                                    aqyEquipmentYlRawService.insertAqyEquipmentYlRaw(aqyEquipmentYlRaw);
                                                    ylMap.put(aqyEquipmentList.get(it).getId(), aqyEquipmentYlRaw);
                                                }
                                            }
                                        }
                                    }
                                    aqyAlarmService.checkYlRawDataWillAlarm(ylMap);
                                    // 更新前端数据
                                    WebSocketUsers.pushMessage(5, -1, null);
                                    break;
                            }
                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String json1 =  "{\"A108202412020005\": {\"1734566400000\": \"-3.961844,23.962562,-5.704853,36.030761,-6.522626,56.956599\"}}";
        Map<String, Map<String, Map<String,String>>> bean = JSONObject.parseObject(json1, Map.class);
        Set<Map.Entry<String, Map<String, Map<String, String>>>> entries = bean.entrySet();
        System.out.println(bean);
    }
    @Override
    public String getTopicPattern() {
        return "\\$data/.*/raw";
    }

    private String topic;

    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }
}

@Data
class RawResultData{
    private Map<String, Map<String, String>> data;

    private Integer msgId;
}
