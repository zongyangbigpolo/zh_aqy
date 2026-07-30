package com.ruoyi.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.core.domain.aqy.*;
import com.ruoyi.common.core.domain.aqy.Vo.AqyWyRawChartData;
import com.ruoyi.common.core.domain.aqy.Vo.CharPoint;
import com.ruoyi.common.core.domain.aqy.Vo.ChartXAxisMark;
import com.ruoyi.common.core.domain.aqy.Vo.FrontChartVo;
import com.ruoyi.service.IAqyEquipmentService;
import com.ruoyi.service.IAqyEquipmentTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.service.IAqyEquipmentLfRawService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 裂缝监测设备上传数据记录Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/aqyEquipmentLfRaw")
public class AqyEquipmentLfRawController extends BaseController {
    @Autowired
    private IAqyEquipmentLfRawService aqyEquipmentLfRawService;
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;
    @Autowired
    private IAqyEquipmentTypeService aqyEquipmentTypeService;

    /**
     * 查询裂缝监测设备上传数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentLfRaw:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentLfRaw aqyEquipmentLfRaw) {
        startPage();
        List<AqyEquipmentLfRaw> list = aqyEquipmentLfRawService.selectAqyEquipmentLfRawList(aqyEquipmentLfRaw);
        return getDataTable(list);
    }

    /**
     * 导出裂缝监测设备上传数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentLfRaw:export')")
    @Log(title = "裂缝监测设备上传数据记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentLfRaw aqyEquipmentLfRaw) {
        List<AqyEquipmentLfRaw> list = aqyEquipmentLfRawService.selectAqyEquipmentLfRawList(aqyEquipmentLfRaw);
        ExcelUtil<AqyEquipmentLfRaw> util = new ExcelUtil<AqyEquipmentLfRaw>(AqyEquipmentLfRaw.class);
        util.exportExcel(response, list, "裂缝监测设备上传数据记录数据");
    }

    /**
     * 获取裂缝监测设备上传数据记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentLfRaw:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(aqyEquipmentLfRawService.selectAqyEquipmentLfRawById(id));
    }

    /**
     * 新增裂缝监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentLfRaw:add')")
    @Log(title = "裂缝监测设备上传数据记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentLfRaw aqyEquipmentLfRaw) {
        return toAjax(aqyEquipmentLfRawService.insertAqyEquipmentLfRaw(aqyEquipmentLfRaw));
    }

    /**
     * 修改裂缝监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentLfRaw:edit')")
    @Log(title = "裂缝监测设备上传数据记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentLfRaw aqyEquipmentLfRaw) {
        return toAjax(aqyEquipmentLfRawService.updateAqyEquipmentLfRaw(aqyEquipmentLfRaw));
    }

    /**
     * 删除裂缝监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentLfRaw:remove')")
    @Log(title = "裂缝监测设备上传数据记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aqyEquipmentLfRawService.deleteAqyEquipmentLfRawByIds(ids));
    }

    /**
     * 获取裂缝数据用于前端折线图显示
     * @param aqyEquipmentLfRaw
     * @return
     */
//    @GetMapping("/listLfRawForCharts")
//    public AjaxResult listRawForCharts(AqyEquipmentLfRaw aqyEquipmentLfRaw){
//        AqyEquipmentType aqyEquipmentType = aqyEquipmentTypeService.selectAqyEquipmentTypeBySymbol(aqyEquipmentLfRaw.getEqmtTypeSymbol());
//        if(aqyEquipmentType == null)
//            return AjaxResult.success();
//
//        AqyEquipment query = new AqyEquipment();
//        query.setProjectId(aqyEquipmentLfRaw.getProjectId());
//        query.setEqmtTypeId(aqyEquipmentType.getId());
//        List<AqyEquipment> aqyEquipments = aqyEquipmentService.selectAqyEquipmentList(query);
//        if(aqyEquipments == null || aqyEquipments.size() == 0)
//            return AjaxResult.success();
//
//        List<AqyWyRawChartData> eqmtRawList = new ArrayList<>();
//        List<ChartXAxisMark> xAxisMarks;
//        AqyWyRawChartData rawItem;
//        List<CharPoint> charPointsX;
//        CharPoint point;
//        for (AqyEquipment aqyEquipment : aqyEquipments) {
//            rawItem = new AqyWyRawChartData();
//            rawItem.setEqmtId(aqyEquipment.getId());
//            rawItem.setEqmtName(aqyEquipment.getEqmtName());
//            rawItem.setUnitName(aqyEquipment.getUnitName());
//            aqyEquipmentLfRaw.setEqmtId(aqyEquipment.getId());
//            List<AqyEquipmentLfRaw> list = aqyEquipmentLfRawService.listRawForCharts(aqyEquipmentLfRaw);
//            charPointsX = new ArrayList<>();
//            xAxisMarks = new ArrayList<>();
//            if(list != null && list.size() > 0){
//                Long startTime = list.get(0).getCatchTime();
//                for (AqyEquipmentLfRaw equipmentRaw : list) {
//                    charPointsX.add(point = new CharPoint(equipmentRaw.getCatchTime(), startTime, equipmentRaw.getLfValue()));
//
//                    xAxisMarks.add(new ChartXAxisMark(point.getCatchTimeMark(), point.getCatchTimeInterval()));
//                }
//            }
//            rawItem.setValuesX(charPointsX);
//            rawItem.setXAxisMarks(xAxisMarks);
//            eqmtRawList.add(rawItem);
//        }
//
//        return AjaxResult.success().put("items", eqmtRawList);
//    }

    @GetMapping("/listLfRawForCharts")
    public AjaxResult listRawForCharts(AqyEquipmentLfRaw aqyEquipmentLfRaw){
        List<FrontChartVo> rawDataList = new ArrayList<>();
        List<AqyEquipment> eqmts = aqyEquipmentService.selectAqyEqmtsByType("LF");
        if(CollectionUtil.isNotEmpty(eqmts)) {
            for (AqyEquipment eqmt : eqmts) {
                if(eqmt.getInitialX() == null)
                    throw new RuntimeException("设备【" + eqmt.getEqmtName() + "】没有设置初始值");

                AqyEquipmentLfRaw lfRaw = aqyEquipmentLfRawService.selectLastDataByEqmtId(eqmt.getId());
                FrontChartVo frontChartVo = new FrontChartVo();
                frontChartVo.setSortNum(eqmt.getSortNum());
                frontChartVo.setName("裂缝" + eqmt.getSortNum());
                frontChartVo.setValueLf(lfRaw != null ? lfRaw.getLfValue().subtract(eqmt.getInitialX()) : BigDecimal.valueOf(0L));
                rawDataList.add(frontChartVo);
            }
        }
        rawDataList.sort(Comparator.comparing(FrontChartVo::getSortNum));
        return AjaxResult.success().put("items", rawDataList);
    }
}
