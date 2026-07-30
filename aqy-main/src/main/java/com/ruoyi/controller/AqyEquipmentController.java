package com.ruoyi.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import com.ruoyi.common.core.domain.aqy.AqyEquipmentType;
import com.ruoyi.common.core.domain.aqy.Vo.AqyEqmtAlarmVo;
import com.ruoyi.common.core.domain.aqy.Vo.AqyTypeEqmtVo;
import com.ruoyi.common.utils.StringUtils;
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
import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.service.IAqyEquipmentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 数据采集设备Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/aqyEquipment")
public class AqyEquipmentController extends BaseController {
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;
    @Autowired
    private IAqyEquipmentTypeService equipmentTypeService;

    /**
     * 查询数据采集设备列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipment:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipment aqyEquipment) {
        startPage();
        List<AqyEquipment> list = aqyEquipmentService.selectAqyEquipmentList(aqyEquipment);
        return getDataTable(list);
    }

    /**
     * 导出数据采集设备列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipment:export')")
    @Log(title = "数据采集设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipment aqyEquipment) {
        List<AqyEquipment> list = aqyEquipmentService.selectAqyEquipmentList(aqyEquipment);
        ExcelUtil<AqyEquipment> util = new ExcelUtil<AqyEquipment>(AqyEquipment.class);
        util.exportExcel(response, list, "数据采集设备数据");
    }

    /**
     * 获取数据采集设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(aqyEquipmentService.selectAqyEquipmentById(id));
    }

    /**
     * 新增数据采集设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipment:add')")
    @Log(title = "数据采集设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipment aqyEquipment) {
        return toAjax(aqyEquipmentService.insertAqyEquipment(aqyEquipment));
    }

    /**
     * 修改数据采集设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipment:edit')")
    @Log(title = "数据采集设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipment aqyEquipment) {
        return toAjax(aqyEquipmentService.updateAqyEquipment(aqyEquipment));
    }

    /**
     * 删除数据采集设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipment:remove')")
    @Log(title = "数据采集设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aqyEquipmentService.deleteAqyEquipmentByIds(ids));
    }

    /**
     * 查询数据采集设备列表，并以设备类型分组
     */
    @GetMapping("/listEqmtsGroupByType")
    public AjaxResult listEqmtsGroupByType(AqyEquipment aqyEquipment){
        List<AqyEquipment> aqyEquipments = aqyEquipmentService.selectAqyEquipmentList(aqyEquipment);
        Map<Long, List<AqyEquipment>> collect = aqyEquipments.stream().collect(Collectors.groupingBy(AqyEquipment::getEqmtTypeId));
        Map<Long, AqyTypeEqmtVo> typeEqmts = new HashMap<>();
        if(collect != null && collect.size() > 0){
            AqyTypeEqmtVo item;
            Integer theOnlineCount;
            String unitName;
            for (Map.Entry<Long, List<AqyEquipment>> typeItem : collect.entrySet()) {
                BigDecimal valueX = new BigDecimal(0);
                BigDecimal valueY = new BigDecimal(0);
                BigDecimal valueZ = new BigDecimal(0);
                unitName = "";
                theOnlineCount = 0;
                if(typeItem.getValue() != null && typeItem.getValue().size() > 0){
                    for (AqyEquipment equipment : typeItem.getValue()) {
                        if(equipment.getOnlineStatus() != null && equipment.getOnlineStatus() == 0)
                            theOnlineCount++;

                        if(equipment.getAccumulativeChangeValueX() != null && Math.abs(equipment.getAccumulativeChangeValueX().doubleValue()) > valueX.doubleValue())
                            valueX = equipment.getAccumulativeChangeValueX();
                        if(equipment.getAccumulativeChangeValueY() != null && Math.abs(equipment.getAccumulativeChangeValueY().doubleValue()) > valueY.doubleValue())
                            valueY = equipment.getAccumulativeChangeValueY();
                        if(equipment.getAccumulativeChangeValueH() != null && Math.abs(equipment.getAccumulativeChangeValueH().doubleValue()) > valueZ.doubleValue())
                            valueZ = equipment.getAccumulativeChangeValueH();
                    }

                    unitName = typeItem.getValue().get(0).getUnitName();
                }

                AqyEquipmentType aqyEquipmentType = equipmentTypeService.selectAqyEquipmentTypeById(typeItem.getKey());
                item = new AqyTypeEqmtVo();
                item.setEqmtTypeId(typeItem.getKey());
                item.setEqmtTypeName(aqyEquipmentType.getEqmtTypeName());
                item.setEqmtTypeSymbol(aqyEquipmentType.getEqmtTypeSymbol());
                item.setTotalCount(typeItem.getValue() != null ? typeItem.getValue().size() : 0);
                item.setOnlineCount(theOnlineCount);
                item.setAccumulativeChangeValueX(valueX);
                item.setAccumulativeChangeValueY(valueY);
                item.setAccumulativeChangeValueZ(valueZ);
                item.setUnitName(unitName);
                typeEqmts.put(typeItem.getKey(), item);
            }
        }
        return AjaxResult.success().put("items", typeEqmts.values());
    }

    /**
     * 查询所有设备的报警状态
     * @param aqyEquipment
     * @return
     */
    @GetMapping("/queryEquipmentAlarmStatus")
    public AjaxResult queryEquipmentAlarmStatus(AqyEquipment aqyEquipment){
        List<AqyEqmtAlarmVo> aqyEquipments = aqyEquipmentService.selectAqyEquipmentAlarmStatusList(aqyEquipment);
        boolean hasAlarm = false;
        Integer maxLevel = 100;
        if(aqyEquipments != null && aqyEquipments.size() > 0){
            for (AqyEqmtAlarmVo equipment : aqyEquipments) {
                if(equipment.getMaxAlarmLevel() < maxLevel){
                    maxLevel = equipment.getMaxAlarmLevel();
                }
            }
            hasAlarm = true;
        }

        return AjaxResult.success().put("hasAlarm", hasAlarm).put("items", aqyEquipments).put("maxLevel", maxLevel);
    }

    /**
     * 查询所有设备的报警状态
     * @param aqyEquipment
     * @return
     */
    @GetMapping("/selectAqyEquipmentListForReport")
    public AjaxResult selectAqyEquipmentListForReport(AqyEquipment aqyEquipment){
        List<AqyEqmtAlarmVo> aqyEquipments = aqyEquipmentService.selectAqyEquipmentListForReport(aqyEquipment);
        return AjaxResult.success().put("items", aqyEquipments);
    }
}
