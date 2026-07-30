package com.ruoyi.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.aqy.*;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author：MXJ
 * @Date：2024/10/23 13:49
 */
@RestController
@RequestMapping("/aqy/rawReport")
public class AqyRawReportController extends BaseController {
    @Autowired
    private IAqyEquipmentWyRawService wyRawService;
    @Autowired
    private IAqyEquipmentLfRawService lfRawService;
    @Autowired
    private IAqyEquipmentQjRawService qjRawService;
    @Autowired
    private IAqyEquipmentYlRawService ylRawService;
    @Autowired
    private IAqyEquipmentTypeService equipmentTypeService;

    /**
     * 查询实时数据
     */
    @GetMapping("/listRealTime")
    public TableDataInfo list(AqyRawReport rawReport) {
        convertTimestamp(rawReport);
        AqyEquipmentType aqyEquipmentType = equipmentTypeService.selectAqyEquipmentTypeById(rawReport.getEqmtTypeId());
        switch (aqyEquipmentType.getEqmtTypeSymbol()){
            case "WY":
                startPage();
                List<AqyEquipmentWyRaw> wyList = wyRawService.selectWyRawListForReport(rawReport);
                return getDataTable(wyList);
            case "LF":
                startPage();
                List<AqyEquipmentLfRaw> lfList = lfRawService.selectLfRawListForReport(rawReport);
                return getDataTable(lfList);
            case "YL":
                startPage();
                List<AqyEquipmentYlRaw> ylList = ylRawService.selectYlRawListForReport(rawReport);
                return getDataTable(ylList);
            case "QJ":
                startPage();
                List<AqyEquipmentQjRaw> qjList = qjRawService.selectQjRawListForReport(rawReport);
                return getDataTable(qjList);
            default:
                return getDataTable(new ArrayList<>());
        }
    }

    private void convertTimestamp(AqyRawReport rawReport) {
            rawReport.setStartTime(rawReport.getTimeFrame()[0].getTime());
            rawReport.setEndTime(rawReport.getTimeFrame()[1].getTime());

    }


}
