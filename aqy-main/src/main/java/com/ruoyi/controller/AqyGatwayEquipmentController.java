package com.ruoyi.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.core.domain.aqy.AqyGatwayEquipment;
import com.ruoyi.service.IAqyGatwayEquipmentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 智能网关设备Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/gatwayEquipment")
public class AqyGatwayEquipmentController extends BaseController
{
    @Autowired
    private IAqyGatwayEquipmentService aqyGatwayEquipmentService;

    /**
     * 查询智能网关设备列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:gatwayEquipment:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyGatwayEquipment aqyGatwayEquipment)
    {
        startPage();
        List<AqyGatwayEquipment> list = aqyGatwayEquipmentService.selectAqyGatwayEquipmentList(aqyGatwayEquipment);
        return getDataTable(list);
    }

    /**
     * 导出智能网关设备列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:gatwayEquipment:export')")
    @Log(title = "智能网关设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyGatwayEquipment aqyGatwayEquipment)
    {
        List<AqyGatwayEquipment> list = aqyGatwayEquipmentService.selectAqyGatwayEquipmentList(aqyGatwayEquipment);
        ExcelUtil<AqyGatwayEquipment> util = new ExcelUtil<AqyGatwayEquipment>(AqyGatwayEquipment.class);
        util.exportExcel(response, list, "智能网关设备数据");
    }

    /**
     * 获取智能网关设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:gatwayEquipment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyGatwayEquipmentService.selectAqyGatwayEquipmentById(id));
    }

    /**
     * 新增智能网关设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:gatwayEquipment:add')")
    @Log(title = "智能网关设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyGatwayEquipment aqyGatwayEquipment)
    {
        return toAjax(aqyGatwayEquipmentService.insertAqyGatwayEquipment(aqyGatwayEquipment));
    }

    /**
     * 修改智能网关设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:gatwayEquipment:edit')")
    @Log(title = "智能网关设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyGatwayEquipment aqyGatwayEquipment)
    {
        return toAjax(aqyGatwayEquipmentService.updateAqyGatwayEquipment(aqyGatwayEquipment));
    }

    /**
     * 删除智能网关设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:gatwayEquipment:remove')")
    @Log(title = "智能网关设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyGatwayEquipmentService.deleteAqyGatwayEquipmentByIds(ids));
    }
}
