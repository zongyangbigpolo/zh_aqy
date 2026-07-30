package com.ruoyi.controller;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentType;
import com.ruoyi.common.core.domain.aqy.Vo.AqyWyRawChartData;
import com.ruoyi.common.core.domain.aqy.Vo.CharPoint;
import com.ruoyi.common.core.domain.aqy.Vo.ChartXAxisMark;
import com.ruoyi.common.core.domain.aqy.Vo.FrontChartVo;
import com.ruoyi.service.IAqyEquipmentService;
import com.ruoyi.service.IAqyEquipmentTypeService;
import lombok.val;
import org.apache.poi.hpsf.Decimal;
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
import com.ruoyi.common.core.domain.aqy.AqyEquipmentWyRaw;
import com.ruoyi.service.IAqyEquipmentWyRawService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 位移监测设备上传数据记录Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/aqyEquipmentWyRaw")
public class AqyEquipmentWyRawController extends BaseController {
    @Autowired
    private IAqyEquipmentWyRawService aqyEquipmentWyRawService;
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;
    @Autowired
    private IAqyEquipmentTypeService aqyEquipmentTypeService;

    /**
     * 查询位移监测设备上传数据记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        startPage();
        List<AqyEquipmentWyRaw> list = aqyEquipmentWyRawService.selectAqyEquipmentWyRawList(aqyEquipmentWyRaw);
        return getDataTable(list);
    }

    /**
     * 导出位移监测设备上传数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentWyRaw:export')")
    @Log(title = "位移监测设备上传数据记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        List<AqyEquipmentWyRaw> list = aqyEquipmentWyRawService.selectAqyEquipmentWyRawList(aqyEquipmentWyRaw);
        ExcelUtil<AqyEquipmentWyRaw> util = new ExcelUtil<AqyEquipmentWyRaw>(AqyEquipmentWyRaw.class);
        util.exportExcel(response, list, "位移监测设备上传数据记录数据");
    }

    /**
     * 获取位移监测设备上传数据记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentWyRaw:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(aqyEquipmentWyRawService.selectAqyEquipmentWyRawById(id));
    }

    /**
     * 新增位移监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentWyRaw:add')")
    @Log(title = "位移监测设备上传数据记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        return toAjax(aqyEquipmentWyRawService.insertAqyEquipmentWyRaw(aqyEquipmentWyRaw));
    }

    /**
     * 修改位移监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentWyRaw:edit')")
    @Log(title = "位移监测设备上传数据记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentWyRaw aqyEquipmentWyRaw) {
        return toAjax(aqyEquipmentWyRawService.updateAqyEquipmentWyRaw(aqyEquipmentWyRaw));
    }

    /**
     * 删除位移监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentWyRaw:remove')")
    @Log(title = "位移监测设备上传数据记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aqyEquipmentWyRawService.deleteAqyEquipmentWyRawByIds(ids));
    }

//    /**
//     * 获取位移数据用于前端折线图显示
//     * @param aqyEquipmentWyRaw
//     * @return
//     */
//    @GetMapping("/listWyRawForCharts")
//    public AjaxResult listRawForCharts(AqyEquipmentWyRaw aqyEquipmentWyRaw){
//        AqyEquipmentType aqyEquipmentType = aqyEquipmentTypeService.selectAqyEquipmentTypeBySymbol(aqyEquipmentWyRaw.getEqmtTypeSymbol());
//        if(aqyEquipmentType == null)
//            return AjaxResult.success();
//
//        AqyEquipment query = new AqyEquipment();
//        query.setProjectId(aqyEquipmentWyRaw.getProjectId());
//        query.setEqmtTypeId(aqyEquipmentType.getId());
//        List<AqyEquipment> aqyEquipments = aqyEquipmentService.selectAqyEquipmentList(query);
//        if(aqyEquipments == null || aqyEquipments.size() == 0)
//            return AjaxResult.success();
//
//        List<AqyWyRawChartData> eqmtRawList = new ArrayList<>();
//        List<ChartXAxisMark> xAxisMarks;
//        AqyWyRawChartData rawItem;
//        List<CharPoint> charPointsX, charPointsY;
//        CharPoint point;
//        for (AqyEquipment aqyEquipment : aqyEquipments) {
//            rawItem = new AqyWyRawChartData();
//            rawItem.setEqmtId(aqyEquipment.getId());
//            rawItem.setEqmtName(aqyEquipment.getEqmtName());
//            rawItem.setUnitName(aqyEquipment.getUnitName());
//            aqyEquipmentWyRaw.setEqmtId(aqyEquipment.getId());
//            List<AqyEquipmentWyRaw> list = aqyEquipmentWyRawService.listRawForCharts(aqyEquipmentWyRaw);
//            charPointsX = new ArrayList<>();
//            charPointsY = new ArrayList<>();
//            xAxisMarks = new ArrayList<>();
//            if(list != null && list.size() > 0){
//                Long startTime = list.get(0).getCatchTime();
//                for (AqyEquipmentWyRaw equipmentWyRaw : list) {
//                    if(equipmentWyRaw.getXOrY().equals("X"))
//                        charPointsX.add(point = new CharPoint(equipmentWyRaw.getCatchTime(), startTime, equipmentWyRaw.getValueWy()));
//                    else if(equipmentWyRaw.getXOrY().equals("Y"))
//                        charPointsY.add(point = new CharPoint(equipmentWyRaw.getCatchTime(), startTime, equipmentWyRaw.getValueWy()));
//                    else
//                        throw new RuntimeException("没有设置X_or_Y字段值");
//
//                    xAxisMarks.add(new ChartXAxisMark(point.getCatchTimeMark(), point.getCatchTimeInterval()));
//                }
//            }
//            rawItem.setValuesX(charPointsX);
//            rawItem.setValuesY(charPointsY);
//            rawItem.setXAxisMarks(xAxisMarks);
//            eqmtRawList.add(rawItem);
//        }
//
//        return AjaxResult.success().put("items", eqmtRawList);
//    }

    /**
     * 获取位移数据用于前端折线图显示
     * @param aqyEquipmentWyRaw
     * @return
     */
    @GetMapping("/listWyRawForCharts")
    public AjaxResult listRawForCharts(AqyEquipmentWyRaw aqyEquipmentWyRaw){
        List<FrontChartVo> rawDataList = new ArrayList<>();
        List<AqyEquipment> eqmts = aqyEquipmentService.selectAqyEqmtsByType("WY");
        if(CollectionUtil.isNotEmpty(eqmts)) {
            for (AqyEquipment eqmt : eqmts) {
                if(eqmt.getInitialX() == null)
                    throw new RuntimeException("设备【" + eqmt.getEqmtName() + "】没有设置初始值");

                AqyEquipmentWyRaw wyRaw = aqyEquipmentWyRawService.selectLastDataByEqmtId(eqmt.getId());
                FrontChartVo frontChartVo = new FrontChartVo();
                frontChartVo.setEqmtId(eqmt.getId());
                frontChartVo.setSortNum(eqmt.getSortNum());
                frontChartVo.setName("靶标" + eqmt.getSortNum());
                frontChartVo.setXOrY(eqmt.getXOrY());
                frontChartVo.setValueWy(wyRaw != null ? (wyRaw.getValueWy().subtract(eqmt.getInitialX())) : BigDecimal.valueOf(0L));
                rawDataList.add(frontChartVo);
            }
        }
        rawDataList.sort(Comparator.comparing(FrontChartVo::getSortNum));
        return AjaxResult.success().put("items", rawDataList);
    }
}
