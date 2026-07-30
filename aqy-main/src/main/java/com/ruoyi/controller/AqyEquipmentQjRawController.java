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
import com.ruoyi.service.IAqyEquipmentQjRawService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 倾角监测设备上传数据记录Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/aqyEquipmentQjRaw")
public class AqyEquipmentQjRawController extends BaseController {
    @Autowired
    private IAqyEquipmentQjRawService aqyEquipmentQjRawService;
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;
    @Autowired
    private IAqyEquipmentTypeService aqyEquipmentTypeService;

    /**
     * 查询倾角监测设备上传数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentQjRaw:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentQjRaw aqyEquipmentQjRaw) {
        startPage();
        List<AqyEquipmentQjRaw> list = aqyEquipmentQjRawService.selectAqyEquipmentQjRawList(aqyEquipmentQjRaw);
        return getDataTable(list);
    }

    /**
     * 导出倾角监测设备上传数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentQjRaw:export')")
    @Log(title = "倾角监测设备上传数据记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentQjRaw aqyEquipmentQjRaw) {
        List<AqyEquipmentQjRaw> list = aqyEquipmentQjRawService.selectAqyEquipmentQjRawList(aqyEquipmentQjRaw);
        ExcelUtil<AqyEquipmentQjRaw> util = new ExcelUtil<AqyEquipmentQjRaw>(AqyEquipmentQjRaw.class);
        util.exportExcel(response, list, "倾角监测设备上传数据记录数据");
    }

    /**
     * 获取倾角监测设备上传数据记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentQjRaw:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(aqyEquipmentQjRawService.selectAqyEquipmentQjRawById(id));
    }

    /**
     * 新增倾角监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentQjRaw:add')")
    @Log(title = "倾角监测设备上传数据记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentQjRaw aqyEquipmentQjRaw) {
        return toAjax(aqyEquipmentQjRawService.insertAqyEquipmentQjRaw(aqyEquipmentQjRaw));
    }

    /**
     * 修改倾角监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentQjRaw:edit')")
    @Log(title = "倾角监测设备上传数据记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentQjRaw aqyEquipmentQjRaw) {
        return toAjax(aqyEquipmentQjRawService.updateAqyEquipmentQjRaw(aqyEquipmentQjRaw));
    }

    /**
     * 删除倾角监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentQjRaw:remove')")
    @Log(title = "倾角监测设备上传数据记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aqyEquipmentQjRawService.deleteAqyEquipmentQjRawByIds(ids));
    }

    /**
     * 获取倾角数据用于前端折线图显示
     * @param aqyEquipmentQjRaw
     * @return
     */
//    @GetMapping("/listQjRawForCharts")
//    public AjaxResult listRawForCharts(AqyEquipmentQjRaw aqyEquipmentQjRaw){
//        AqyEquipmentType aqyEquipmentType = aqyEquipmentTypeService.selectAqyEquipmentTypeBySymbol(aqyEquipmentQjRaw.getEqmtTypeSymbol());
//        if(aqyEquipmentType == null)
//            return AjaxResult.success();
//
//        AqyEquipment query = new AqyEquipment();
//        query.setProjectId(aqyEquipmentQjRaw.getProjectId());
//        query.setEqmtTypeId(aqyEquipmentType.getId());
//        List<AqyEquipment> aqyEquipments = aqyEquipmentService.selectAqyEquipmentList(query);
//        if(aqyEquipments == null || aqyEquipments.size() == 0)
//            return AjaxResult.success();
//
//        List<AqyWyRawChartData> eqmtRawList = new ArrayList<>();
//        List<ChartXAxisMark> xAxisMarks;
//        AqyWyRawChartData rawItem;
//        List<CharPoint> charPointsX, charPointsY, charPointsZ;
//        CharPoint point;
//        for (AqyEquipment aqyEquipment : aqyEquipments) {
//            rawItem = new AqyWyRawChartData();
//            rawItem.setEqmtId(aqyEquipment.getId());
//            rawItem.setEqmtName(aqyEquipment.getEqmtName());
//            rawItem.setUnitName(aqyEquipment.getUnitName());
//            aqyEquipmentQjRaw.setEqmtId(aqyEquipment.getId());
//            List<AqyEquipmentQjRaw> list = aqyEquipmentQjRawService.listRawForCharts(aqyEquipmentQjRaw);
//            charPointsX = new ArrayList<>();
//            charPointsY = new ArrayList<>();
//            charPointsZ = new ArrayList<>();
//            xAxisMarks = new ArrayList<>();
//            if(list != null && list.size() > 0){
//                Long startTime = list.get(0).getCatchTime();
//                for (AqyEquipmentQjRaw equipmentWyRaw : list) {
//                    charPointsX.add(point = new CharPoint(equipmentWyRaw.getCatchTime(), startTime, equipmentWyRaw.getXValueQj()));
//                    charPointsY.add(new CharPoint(equipmentWyRaw.getCatchTime(), startTime, equipmentWyRaw.getYValueQj()));
//                    charPointsZ.add(new CharPoint(equipmentWyRaw.getCatchTime(), startTime, equipmentWyRaw.getZValueQj()));
//
//                    xAxisMarks.add(new ChartXAxisMark(point.getCatchTimeMark(), point.getCatchTimeInterval()));
//                }
//            }
//            rawItem.setValuesX(charPointsX);
//            rawItem.setValuesY(charPointsY);
//            rawItem.setValuesZ(charPointsZ);
//            rawItem.setXAxisMarks(xAxisMarks);
//            eqmtRawList.add(rawItem);
//        }
//
//        return AjaxResult.success().put("items", eqmtRawList);
//    }

    @GetMapping("/listQjRawForCharts")
    public AjaxResult listRawForCharts(AqyEquipmentQjRaw aqyEquipmentQjRaw){
        List<FrontChartVo> rawDataList = new ArrayList<>();
        List<AqyEquipment> eqmts = aqyEquipmentService.selectAqyEqmtsByType("QJ");
        if(CollectionUtil.isNotEmpty(eqmts)) {
            for (AqyEquipment eqmt : eqmts) {
                if(eqmt.getInitialX() == null || eqmt.getInitialY() == null || eqmt.getInitialH() == null)
                    throw new RuntimeException("设备【" + eqmt.getEqmtName() + "】没有设置初始值");

                AqyEquipmentQjRaw qjRaw = aqyEquipmentQjRawService.selectLastDataByEqmtId(eqmt.getId());
                FrontChartVo frontChartVo = new FrontChartVo();
                frontChartVo.setSortNum(eqmt.getSortNum());
                frontChartVo.setName("倾角" + eqmt.getSortNum());
                frontChartVo.setValueQjX(qjRaw != null ? qjRaw.getXValueQj().subtract(eqmt.getInitialX()) : BigDecimal.valueOf(0L));
                frontChartVo.setValueQjY(qjRaw != null ? qjRaw.getYValueQj().subtract(eqmt.getInitialY()) : BigDecimal.valueOf(0L));
                frontChartVo.setValueQjZ(qjRaw != null ? qjRaw.getZValueQj().subtract(eqmt.getInitialH()) : BigDecimal.valueOf(0L));
                rawDataList.add(frontChartVo);
            }
        }
        rawDataList.sort(Comparator.comparing(FrontChartVo::getSortNum));
        return AjaxResult.success().put("items", rawDataList);
    }
}
