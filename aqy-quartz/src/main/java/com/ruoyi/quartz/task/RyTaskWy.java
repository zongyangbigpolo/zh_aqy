package com.ruoyi.quartz.task;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.aqy.*;
import com.ruoyi.common.core.domain.aqy.Vo.WebSocketNodeVo;
import com.ruoyi.common.hikapi.GNSSApi;
import com.ruoyi.common.hikapi.Params;
import com.ruoyi.common.sdrkapi.SDRKApi;
import com.ruoyi.common.tongganyunapi.TGYApi;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.websocket.WebSocketUsers;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.service.ISysJobService;
import com.ruoyi.service.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/12/20 14:54
 */
@Component("RyTaskWy")
@Slf4j
public class RyTaskWy {
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;
    @Autowired
    private IAqyEquipmentWyRawService aqyEquipmentWyRawService;
    @Autowired
    private ISysJobService jobService;
    @Autowired
    private IAqyAlarmService aqyAlarmService;

    @Autowired
    private IAqySdrkWyService aqySdrkWyService;

    @Autowired
    private IAqyHikWyService aqyHikWyService;

    private static final String deviceAddr = "40353024";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 获取位移数据
     */
    public void getX1WyData() throws Exception {
        catchData("RyTaskWy.getX1WyData()");
    }

    public void getY1WyData() throws Exception {
        catchData("RyTaskWy.getY1WyData()");
    }

    public void getX2WyData() throws Exception {
        catchData("RyTaskWy.getX2WyData()");
    }

    public void getY2WyData() throws Exception {
        catchData("RyTaskWy.getY2WyData()");
    }

    public void getX3WyData() throws Exception {
        catchData("RyTaskWy.getX3WyData()");
    }

    public void getY3WyData() throws Exception {
        catchData("RyTaskWy.getY3WyData()");
    }

    public void getX4WyData() throws Exception {
        catchData("RyTaskWy.getX4WyData()");
    }

    public void getY4WyData() throws Exception {
        catchData("RyTaskWy.getY4WyData()");
    }

    public void getX5WyData() throws Exception {
        catchData("RyTaskWy.getX5WyData()");
    }

    public void getY5WyData() throws Exception {
        catchData("RyTaskWy.getY5WyData()");
    }

    public void getX6WyData() throws Exception {
        catchData("RyTaskWy.getX6WyData()");
    }

    public void getY6WyData() throws Exception {
        catchData("RyTaskWy.getY6WyData()");
    }

    public void getX7WyData() throws Exception {
        catchData("RyTaskWy.getX7WyData()");
    }

    public void getY7WyData() throws Exception {
        catchData("RyTaskWy.getY7WyData()");
    }

    public void getX8WyData() throws Exception {
        catchData("RyTaskWy.getX8WyData()");
    }

    public void getY8WyData() throws Exception {
        catchData("RyTaskWy.getY8WyData()");
    }

    public void getX9WyData() throws Exception {
        catchData("RyTaskWy.getX9WyData()");
    }

    public void getY9WyData() throws Exception {
        catchData("RyTaskWy.getY9WyData()");
    }

    public void getX10WyData() throws Exception {
        catchData("RyTaskWy.getX10WyData()");
    }

    public void getY10WyData() throws Exception {
        catchData("RyTaskWy.getY10WyData()");
    }

    public void getX11WyData() throws Exception {
        catchData("RyTaskWy.getX11WyData()");
    }

    public void getY11WyData() throws Exception {
        catchData("RyTaskWy.getY11WyData()");
    }

    public void getX12WyData() throws Exception {
        catchData("RyTaskWy.getX12WyData()");
    }

    public void getY12WyData() throws Exception {
        catchData("RyTaskWy.getY12WyData()");
    }

    public void getX13WyData() throws Exception {
        catchData("RyTaskWy.getX13WyData()");
    }

    public void getY13WyData() throws Exception {
        catchData("RyTaskWy.getY13WyData()");
    }

    public void getX14WyData() throws Exception {
        catchData("RyTaskWy.getX14WyData()");
    }

    public void getY14WyData() throws Exception {
        catchData("RyTaskWy.getY14WyData()");
    }

    public void getX15WyData() throws Exception {
        catchData("RyTaskWy.getX15WyData()");
    }

    public void getY15WyData() throws Exception {
        catchData("RyTaskWy.getY15WyData()");
    }


    /**
     * 获取数据
     *
     * @param invokeTarget
     * @throws Exception 查询设备单位秒
     *                   存到数据库为毫秒
     */
    private void catchData(String invokeTarget) throws Exception {
        SysJob queryJob = new SysJob();
        queryJob.setInvokeTarget(invokeTarget);
        List<SysJob> sysJobs = jobService.selectJobList(queryJob);
        Map<Long, AqyEquipmentWyRaw> wyMap = new HashMap<>();
        Long startTime = DateUtils.addHours(DateUtils.getNowDate(), -1).getTime() / 1000;
        Long endTime = DateUtils.addHours(DateUtils.getNowDate(), 1).getTime() / 1000;
        WebSocketNodeVo webSocketNodeVo = null;
        AqyEquipmentWyRaw wyRaw = null;
        if (CollectionUtil.isNotEmpty(sysJobs)) {
            AqyEquipment queryEqmt = new AqyEquipment();
            queryEqmt.setQrtzJobId(sysJobs.get(0).getJobId());
            List<AqyEquipment> eqmtList = aqyEquipmentService.selectAqyEquipmentList(queryEqmt);
            if (CollectionUtil.isNotEmpty(eqmtList)) {
//                JSONArray jsonArray = TGYApi.getWyData(Long.parseLong(eqmtList.get(0).getEqmtCode()), Long.valueOf("1736148214"), Long.valueOf("1736209449"));
                JSONArray jsonArray = TGYApi.getWyData(Long.parseLong(eqmtList.get(0).getEqmtCode()), startTime, endTime);
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    Long catchTime = jsonArray.getJSONObject(jsonArray.size() - 1).getLongValue("time") * 1000;
                    if (aqyEquipmentWyRawService.isCheckData(eqmtList.get(0).getEqmtCode(), catchTime) == 0) {
                        String wyPictrue = getWyPictrue(eqmtList.get(0).getVisualEqmtCode(), startTime, endTime);//获取位移照片
                        wyRaw = new AqyEquipmentWyRaw();
                        wyRaw.setEqmtId(eqmtList.get(0).getId());
                        wyRaw.setEqmtCode(eqmtList.get(0).getEqmtCode());
                        wyRaw.setEqmtName(eqmtList.get(0).getEqmtName());
                        wyRaw.setValueWy(jsonArray.getJSONObject(jsonArray.size() - 1).getBigDecimal("value"));
                        wyRaw.setCatchTime(catchTime);
                        wyRaw.setCreateTime(DateUtils.getNowDate());
                        wyRaw.setXOrY(eqmtList.get(0).getXOrY());
                        wyRaw.setPicture(wyPictrue);
                        aqyEquipmentWyRawService.insertAqyEquipmentWyRaw(wyRaw);
                        wyMap.put(eqmtList.get(0).getId(), wyRaw);
                    }
//                    for (int i = 0; i < jsonArray.size(); i++) {
//                        Long catchTime = jsonArray.getJSONObject(i).getLongValue("time") * 1000;
//                        if (aqyEquipmentWyRawService.isCheckData(eqmtList.get(0).getEqmtCode(), catchTime) == 0) {
//                            wyRaw = new AqyEquipmentWyRaw();
//                            wyRaw.setEqmtId(eqmtList.get(0).getId());
//                            wyRaw.setEqmtCode(eqmtList.get(0).getEqmtCode());
//                            wyRaw.setEqmtName(eqmtList.get(0).getEqmtName());
//                            wyRaw.setValueWy(jsonArray.getJSONObject(i).getBigDecimal("value"));
//                            wyRaw.setCatchTime(catchTime);
//                            wyRaw.setCreateTime(DateUtils.getNowDate());
//                            wyRaw.setXOrY(eqmtList.get(0).getXOrY());
//                            aqyEquipmentWyRawService.insertAqyEquipmentWyRaw(wyRaw);
//                        }
//                    }
                }
            }
        }
        if (wyRaw != null) {
            webSocketNodeVo = new WebSocketNodeVo(wyRaw.getEqmtId(), wyRaw.getValueWy());
        }
        if (!wyMap.isEmpty()) {
            // 修改采集频率
            Map<Long, Long> eqmtAlarms = aqyAlarmService.checkWyRawDataWillAlarm(wyMap);
            if (!eqmtAlarms.isEmpty()) {
                // 修改采集频率
                for (Map.Entry<Long, Long> entryItem : eqmtAlarms.entrySet()) {
                    AqyEquipment aqyEquipment = aqyEquipmentService.selectAqyEquipmentById(entryItem.getKey());
                    SysJob sysJob = jobService.selectJobById(aqyEquipment.getQrtzJobId());
                    AqyAlarm aqyAlarm = aqyAlarmService.selectAqyAlarmById(entryItem.getValue());
                    if (sysJob != null) {
                        sysJob.setCronExpression(aqyAlarm.getCronExpress());
                        jobService.updateJob(sysJob);
                    }
                    webSocketNodeVo.setEqmtTypeId(aqyEquipment.getEqmtTypeId());
                    webSocketNodeVo.setEqmtTypeName(aqyEquipment.getEqmtTypeData() != null ? aqyEquipment.getEqmtTypeData().getEqmtTypeName() : null);
                    webSocketNodeVo.setAlarmLevel(aqyAlarm.getAlarmLevel());
                    // 更新前端数据
                    WebSocketUsers.pushMessage(2, -1, JSONObject.toJSONString(webSocketNodeVo));
                }
            } else {
                for (Map.Entry<Long, AqyEquipmentWyRaw> entryItem : wyMap.entrySet()) {
                    AqyEquipment aqyEquipment = aqyEquipmentService.selectAqyEquipmentById(entryItem.getKey());
                    SysJob sysJob = jobService.selectJobById(aqyEquipment.getQrtzJobId());
                    if (sysJob != null) {
                        sysJob.setCronExpression("0 0,2,4,6,8,10,12,14,16,18,20 0/1 * * ?");
                        jobService.updateJob(sysJob);
                    }
                    webSocketNodeVo.setEqmtTypeId(aqyEquipment.getEqmtTypeId());
                    webSocketNodeVo.setEqmtTypeName(aqyEquipment.getEqmtTypeData() != null ? aqyEquipment.getEqmtTypeData().getEqmtTypeName() : null);
                    webSocketNodeVo.setAlarmLevel(0);
                    // 更新前端数据
                    WebSocketUsers.pushMessage(2, -1, JSONObject.toJSONString(webSocketNodeVo));
                }
            }
        }
    }

    /**
     * 根据设备型号获取对应的monitorPointId
     */
    private String getMonitorPointIdByDeviceNo(String deviceNo) {
        switch (deviceNo) {
            case "A108202412020002":
                return "1593";
            case "A108202412020005":
                return "1595";
            case "A108202412020029":
                return "1592";
            case "A10820241202002D":
                return "1591";
            case "A10820241202002E":
                return "1594";
            default:
                return null;
        }
    }

    /**
     * 获取位移图
     *
     * @return
     */
    public String getWyPictrue(String visualEqmtCode, Long startTime, Long endTime) throws IOException {
        // 监控事件
        String pictureUrl = "";
        String monitorPointId = getMonitorPointIdByDeviceNo(visualEqmtCode);         // 根据设备型号获取对应的 monitorPointId
        JSONObject jsonObject = TGYApi.monitorEvents(Long.valueOf(85592), startTime * 1000, endTime * 1000);
        if (jsonObject != null) {
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray list = data.getJSONArray("list");
            for (int i = 0; i < list.size(); i++) {
                JSONObject item = list.getJSONObject(i);
                if (item.getString("monitorPointId").equals(monitorPointId)) {
                    JSONArray events = item.getJSONArray("events");
                    if (events != null && !events.isEmpty()) {
                        Long eventId = events.getJSONObject(0).getLong("id");// 最新事件id
                        JSONObject jsonObject1 = TGYApi.monitorEventsDetail(eventId);//获取监控事件详情
                        if (jsonObject1 != null && !jsonObject1.isEmpty()) {
                            JSONObject data1 = jsonObject1.getJSONObject("data");
                            JSONArray pictures = data1.getJSONArray("pictures");
                            if (pictures != null && !pictures.isEmpty()) {
                                pictureUrl = pictures.getJSONObject(0).getString("url");
//                                String s = FileUploadUtils.uploadFromUrl(FileUtils.localFilePath, pictureUrl);
//                                pictureUrl = s;
                            }
                        }

                    }
                }
            }

        }
        return pictureUrl;
    }


    public void getSdrkWyData() throws UnsupportedEncodingException {
        //初始化数据
        AqySdrkWy aqySdrkWy = new AqySdrkWy();

        JSONObject jsonObject = SDRKApi.getRealTimeDataByDeviceAddr(deviceAddr);
        JSONArray dataArray = jsonObject.getJSONArray("data");

        // 获取第一个设备的数据
        JSONObject deviceData = dataArray.getJSONObject(0);
        JSONArray dataItems = deviceData.getJSONArray("dataItem");

        // 用于存储结果
        BigDecimal horizontalDisplacement = BigDecimal.ZERO;
        BigDecimal verticalDisplacement = BigDecimal.ZERO;
        long timestamp = 0L;

        // 获取时间戳
        timestamp = deviceData.getLongValue("timeStamp");
        // 转换成日期格式显示
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);

        System.out.println("时间戳: " + timestamp);
        System.out.println("格式化时间: " + formattedDate);
        aqySdrkWy.setCatchTime(formattedDate);
        aqySdrkWy.setCreateTime(DateUtils.getNowDate());
        aqySdrkWy.setDeviceAddr(deviceAddr);
        // 遍历数据项
        for (int i = 0; i < dataItems.size(); i++) {
            JSONObject item = dataItems.getJSONObject(i);
            int nodeId = item.getIntValue("nodeId");

            if (nodeId == 5 || nodeId == 6) {
                JSONArray registerItems = item.getJSONArray("registerItem");
                JSONObject register = registerItems.getJSONObject(0);
                String data = register.getString("data");
                String registerName = register.getString("registerName");

                if (nodeId == 5) {
                    horizontalDisplacement = new BigDecimal(data);
                    aqySdrkWy.setValueWyX(horizontalDisplacement);
                    System.out.println("水平位移: " + horizontalDisplacement + " " + register.getString("unit"));
                } else {
                    verticalDisplacement = new BigDecimal(data);
                    aqySdrkWy.setValueWyY(verticalDisplacement);
                    System.out.println("垂直位移: " + verticalDisplacement + " " + register.getString("unit"));
                }
            }
        }
        //保存数据
        aqySdrkWyService.insertAqySdrkWy(aqySdrkWy);
    }


    public void getHikWyData() {
        try {
            //初始化数据
            AqyHikWy aqyHikWy = new AqyHikWy();

            Params params = new Params();
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = now.minusMinutes(20);
            LocalDateTime endTime = now;

            // 格式化时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000+08:00");
            String startTimeStr = startTime.format(formatter);
            String endTimeStr = endTime.format(formatter);
            params.setPageNo(1);
            params.setPageSize(100);
            params.setStartTime(startTimeStr);
            params.setEndTime(endTimeStr);

            JSONObject historyMonitorData = GNSSApi.getHistoryMonitorData(params);

            // 获取data对象
            JSONObject data = historyMonitorData.getJSONObject("data");
            if (data == null) {
                System.out.println("数据格式错误：未找到data对象");
                return;
            }

            // 获取list数组
            JSONArray list = data.getJSONArray("list");
            if (list == null || list.isEmpty()) {
                System.out.println("数据为空：list为空或不存在");
                return;
            }


            // 创建日期格式化对象
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 遍历list数组
            for (int i = 0; i < list.size(); i++) {
                JSONObject item = list.getJSONObject(i);

                // 检查deviceId和eventType
                if ("FN8479482".equals(item.getString("deviceId")) &&
                        "monitorGNSS".equals(item.getString("eventType"))) {

                    // 获取并转换时间
                    String originalTime = item.getString("time");
                    String formattedTime = ZonedDateTime.parse(originalTime, inputFormatter)
                            .toLocalDateTime()
                            .format(outputFormatter);


                    // 获取data中的FN8479482数据
                    JSONObject itemData = item.getJSONObject("data");
                    String fn8479482Data = itemData.getString("FN8479482");

                    // 解析内部JSON字符串
                    JSONObject gpData = JSON.parseObject(fn8479482Data);
                    JSONObject l1gp = gpData.getJSONObject("L1_GP");

                    aqyHikWy.setValueWyX(new BigDecimal(l1gp.getString("gpsTotalX")));
                    aqyHikWy.setValueWyY(new BigDecimal(l1gp.getString("gpsTotalY")));
                    aqyHikWy.setValueWyZ(new BigDecimal(l1gp.getString("gpsTotalZ")));
                    aqyHikWy.setDeviceAddr("FN8479482");
                    aqyHikWy.setCatchTime(formattedTime);
                    aqyHikWy.setCreateTime(DateUtils.getNowDate());
                    aqyHikWyService.insertAqyHikWy(aqyHikWy);
                }
            }
        } catch (Exception e) {
            System.out.println("解析数据时发生错误：" + e.getMessage());
            e.printStackTrace();
        }



    }

    public static void main(String[] args) throws IOException {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusMinutes(5);
        LocalDateTime endTime = now;

        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000+08:00");
        String startTimeStr = startTime.format(formatter);
        String endTimeStr = endTime.format(formatter);
        System.out.println(startTimeStr + "---------" + endTimeStr);


//        String fileUrl = "https://monitor-points.obs.cn-east-3.myhuaweicloud.com/1591/1737424800000/img.jpg?AccessKeyId=PY4WQOGYEGW4VQD17HTN&Expires=1737511562&Signature=jePk1DT2guua14SV7DfUIxux3Mw%3D";
//        String result = FileUploadUtils.uploadFromUrl("baseDir", fileUrl);
//        System.out.println("-----" + result);
//        Long startTime = DateUtils.addHours(DateUtils.getNowDate(), -1).getTime() / 1000;
//        Long endTime = DateUtils.addHours(DateUtils.getNowDate(), 1).getTime() / 1000;
//

//        BigDecimal a = new BigDecimal(-8.37);
//        System.out.println(a.subtract(new BigDecimal(-9.36)));
//        System.out.println(Math.abs(-9));
//        Long startTime = DateUtils.addHours(DateUtils.getNowDate(), -2).getTime() / 1000;
//        Long endTime = DateUtils.addHours(DateUtils.getNowDate(), 1).getTime() / 1000;
//        System.out.println(startTime+"---"+endTime);
    }
}
