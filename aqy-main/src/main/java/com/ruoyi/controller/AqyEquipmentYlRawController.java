package com.ruoyi.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentType;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentWyRaw;
import com.ruoyi.common.core.domain.aqy.Vo.AqyWyRawChartData;
import com.ruoyi.common.core.domain.aqy.Vo.CharPoint;
import com.ruoyi.common.core.domain.aqy.Vo.ChartXAxisMark;
import com.ruoyi.common.core.domain.aqy.Vo.FrontChartVo;
import com.ruoyi.service.IAqyEquipmentService;
import com.ruoyi.service.IAqyEquipmentTypeService;
import lombok.val;
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
import com.ruoyi.common.core.domain.aqy.AqyEquipmentYlRaw;
import com.ruoyi.service.IAqyEquipmentYlRawService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 雨量监测设备上传数据记录Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/aqyEquipmentYlRaw")
public class AqyEquipmentYlRawController extends BaseController {
    @Autowired
    private IAqyEquipmentYlRawService aqyEquipmentYlRawService;
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;
    @Autowired
    private IAqyEquipmentTypeService aqyEquipmentTypeService;

    /**
     * 查询雨量监测设备上传数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentYlRaw:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentYlRaw aqyEquipmentYlRaw) {
        startPage();
        List<AqyEquipmentYlRaw> list = aqyEquipmentYlRawService.selectAqyEquipmentYlRawList(aqyEquipmentYlRaw);
        return getDataTable(list);
    }

    /**
     * 导出雨量监测设备上传数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentYlRaw:export')")
    @Log(title = "雨量监测设备上传数据记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentYlRaw aqyEquipmentYlRaw) {
        List<AqyEquipmentYlRaw> list = aqyEquipmentYlRawService.selectAqyEquipmentYlRawList(aqyEquipmentYlRaw);
        ExcelUtil<AqyEquipmentYlRaw> util = new ExcelUtil<AqyEquipmentYlRaw>(AqyEquipmentYlRaw.class);
        util.exportExcel(response, list, "雨量监测设备上传数据记录数据");
    }

    /**
     * 获取雨量监测设备上传数据记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentYlRaw:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(aqyEquipmentYlRawService.selectAqyEquipmentYlRawById(id));
    }

    /**
     * 新增雨量监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentYlRaw:add')")
    @Log(title = "雨量监测设备上传数据记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentYlRaw aqyEquipmentYlRaw) {
        return toAjax(aqyEquipmentYlRawService.insertAqyEquipmentYlRaw(aqyEquipmentYlRaw));
    }

    /**
     * 修改雨量监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentYlRaw:edit')")
    @Log(title = "雨量监测设备上传数据记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentYlRaw aqyEquipmentYlRaw) {
        return toAjax(aqyEquipmentYlRawService.updateAqyEquipmentYlRaw(aqyEquipmentYlRaw));
    }

    /**
     * 删除雨量监测设备上传数据记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentYlRaw:remove')")
    @Log(title = "雨量监测设备上传数据记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aqyEquipmentYlRawService.deleteAqyEquipmentYlRawByIds(ids));
    }

    /**
     * 获取雨量数据用于前端折线图显示
     * @param aqyEquipmentYlRaw
     * @return
     */
//    @GetMapping("/listYlRawForCharts")
//    public AjaxResult listRawForCharts(AqyEquipmentYlRaw aqyEquipmentYlRaw){
//        AqyEquipmentType aqyEquipmentType = aqyEquipmentTypeService.selectAqyEquipmentTypeBySymbol(aqyEquipmentYlRaw.getEqmtTypeSymbol());
//        if(aqyEquipmentType == null)
//            return AjaxResult.success();
//
//        AqyEquipment query = new AqyEquipment();
//        query.setProjectId(aqyEquipmentYlRaw.getProjectId());
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
//            aqyEquipmentYlRaw.setEqmtId(aqyEquipment.getId());
//            List<AqyEquipmentYlRaw> list = aqyEquipmentYlRawService.listRawForCharts(aqyEquipmentYlRaw);
//            charPointsX = new ArrayList<>();
//            xAxisMarks = new ArrayList<>();
//            if(list != null && list.size() > 0){
//                Long startTime = list.get(0).getCatchTime();
//                for (AqyEquipmentYlRaw equipmentRaw : list) {
//                    charPointsX.add(point = new CharPoint(equipmentRaw.getCatchTime(), startTime, equipmentRaw.getYlValue()));
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

    /**
     * 获取位移数据用于前端折线图显示
     * @param aqyEquipmentYlRaw
     * @return
     */
    @GetMapping("/listYlRawForCharts")
    public AjaxResult listRawForCharts(AqyEquipmentYlRaw aqyEquipmentYlRaw) {
        List<FrontChartVo> rawDataList = new ArrayList<>();
        List<AqyEquipmentYlRaw> ylRawList = aqyEquipmentYlRawService.selectLastLimitData(aqyEquipmentYlRaw);
        if(CollectionUtil.isNotEmpty(ylRawList)){
            for (AqyEquipmentYlRaw equipmentYlRaw : ylRawList) {
                AqyEquipment aqyEquipment = aqyEquipmentService.selectAqyEquipmentById(equipmentYlRaw.getEqmtId());
                if(aqyEquipment.getInitialX() == null)
                    throw new RuntimeException("设备【" + aqyEquipment.getEqmtName() + "】没有设置初始值");

                FrontChartVo frontChartVo = new FrontChartVo();
                frontChartVo.setCatchTime(equipmentYlRaw.getCatchTime());
                frontChartVo.setValueYl(equipmentYlRaw.getYlValue().subtract(aqyEquipment.getInitialX()));
                rawDataList.add(frontChartVo);
            }
        }
        rawDataList.sort(Comparator.comparing(FrontChartVo::getCatchTime));
        return AjaxResult.success().put("items", rawDataList);
    }
}
