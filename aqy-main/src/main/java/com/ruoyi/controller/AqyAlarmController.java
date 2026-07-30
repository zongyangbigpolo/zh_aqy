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
import com.ruoyi.common.core.domain.aqy.AqyAlarm;
import com.ruoyi.service.IAqyAlarmService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报警等级Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/alarmLevel")
public class AqyAlarmController extends BaseController
{
    @Autowired
    private IAqyAlarmService aqyAlarmService;

    /**
     * 查询报警等级列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmLevel:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyAlarm aqyAlarm)
    {
        startPage();
        List<AqyAlarm> list = aqyAlarmService.selectAqyAlarmList(aqyAlarm);
        return getDataTable(list);
    }

    /**
     * 导出报警等级列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmLevel:export')")
    @Log(title = "报警等级", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyAlarm aqyAlarm)
    {
        List<AqyAlarm> list = aqyAlarmService.selectAqyAlarmList(aqyAlarm);
        ExcelUtil<AqyAlarm> util = new ExcelUtil<AqyAlarm>(AqyAlarm.class);
        util.exportExcel(response, list, "报警等级数据");
    }

    /**
     * 获取报警等级详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmLevel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyAlarmService.selectAqyAlarmById(id));
    }

    /**
     * 新增报警等级
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmLevel:add')")
    @Log(title = "报警等级", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyAlarm aqyAlarm)
    {
        return toAjax(aqyAlarmService.insertAqyAlarm(aqyAlarm));
    }

    /**
     * 修改报警等级
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmLevel:edit')")
    @Log(title = "报警等级", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyAlarm aqyAlarm)
    {
        return toAjax(aqyAlarmService.updateAqyAlarm(aqyAlarm));
    }

    /**
     * 删除报警等级
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmLevel:remove')")
    @Log(title = "报警等级", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyAlarmService.deleteAqyAlarmByIds(ids));
    }
}
