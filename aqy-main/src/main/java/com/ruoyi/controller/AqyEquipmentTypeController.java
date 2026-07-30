package com.ruoyi.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.core.domain.aqy.AqyProject;
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
import com.ruoyi.common.core.domain.aqy.AqyEquipmentType;
import com.ruoyi.service.IAqyEquipmentTypeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备类型Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/aqyEquipmentType")
public class AqyEquipmentTypeController extends BaseController {
    @Autowired
    private IAqyEquipmentTypeService aqyEquipmentTypeService;

    /**
     * 查询设备类型列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentType:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentType aqyEquipmentType) {
        startPage();
        List<AqyEquipmentType> list = aqyEquipmentTypeService.selectAqyEquipmentTypeList(aqyEquipmentType);
        return getDataTable(list);
    }

    /**
     * 导出设备类型列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentType:export')")
    @Log(title = "设备类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentType aqyEquipmentType) {
        List<AqyEquipmentType> list = aqyEquipmentTypeService.selectAqyEquipmentTypeList(aqyEquipmentType);
        ExcelUtil<AqyEquipmentType> util = new ExcelUtil<AqyEquipmentType>(AqyEquipmentType.class);
        util.exportExcel(response, list, "设备类型数据");
    }

    /**
     * 获取设备类型详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentType:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(aqyEquipmentTypeService.selectAqyEquipmentTypeById(id));
    }

    /**
     * 新增设备类型
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentType:add')")
    @Log(title = "设备类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentType aqyEquipmentType) {
        return toAjax(aqyEquipmentTypeService.insertAqyEquipmentType(aqyEquipmentType));
    }

    /**
     * 修改设备类型
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentType:edit')")
    @Log(title = "设备类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentType aqyEquipmentType) {
        return toAjax(aqyEquipmentTypeService.updateAqyEquipmentType(aqyEquipmentType));
    }

    /**
     * 删除设备类型
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentType:remove')")
    @Log(title = "设备类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aqyEquipmentTypeService.deleteAqyEquipmentTypeByIds(ids));
    }
}
