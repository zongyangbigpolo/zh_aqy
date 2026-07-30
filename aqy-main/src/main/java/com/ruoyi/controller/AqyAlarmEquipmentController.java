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
import com.ruoyi.common.core.domain.aqy.AqyAlarmEquipment;
import com.ruoyi.service.IAqyAlarmEquipmentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 声光报警设备Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/alarmEquipment")
public class AqyAlarmEquipmentController extends BaseController
{
    @Autowired
    private IAqyAlarmEquipmentService aqyAlarmEquipmentService;

    /**
     * 查询声光报警设备列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmEquipment:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyAlarmEquipment aqyAlarmEquipment)
    {
        startPage();
        List<AqyAlarmEquipment> list = aqyAlarmEquipmentService.selectAqyAlarmEquipmentList(aqyAlarmEquipment);
        return getDataTable(list);
    }

    /**
     * 导出声光报警设备列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmEquipment:export')")
    @Log(title = "声光报警设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyAlarmEquipment aqyAlarmEquipment)
    {
        List<AqyAlarmEquipment> list = aqyAlarmEquipmentService.selectAqyAlarmEquipmentList(aqyAlarmEquipment);
        ExcelUtil<AqyAlarmEquipment> util = new ExcelUtil<AqyAlarmEquipment>(AqyAlarmEquipment.class);
        util.exportExcel(response, list, "声光报警设备数据");
    }

    /**
     * 获取声光报警设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmEquipment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyAlarmEquipmentService.selectAqyAlarmEquipmentById(id));
    }

    /**
     * 新增声光报警设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmEquipment:add')")
    @Log(title = "声光报警设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyAlarmEquipment aqyAlarmEquipment)
    {
        return toAjax(aqyAlarmEquipmentService.insertAqyAlarmEquipment(aqyAlarmEquipment));
    }

    /**
     * 修改声光报警设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmEquipment:edit')")
    @Log(title = "声光报警设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyAlarmEquipment aqyAlarmEquipment)
    {
        return toAjax(aqyAlarmEquipmentService.updateAqyAlarmEquipment(aqyAlarmEquipment));
    }

    /**
     * 删除声光报警设备
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmEquipment:remove')")
    @Log(title = "声光报警设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyAlarmEquipmentService.deleteAqyAlarmEquipmentByIds(ids));
    }
}
