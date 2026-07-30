package com.ruoyi.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentTargetData;
import com.ruoyi.service.IAqyEquipmentTargetDataService;
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

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 靶标数据Controller
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
@RestController
@RequestMapping("/aqy/equipmenttargetdata")
public class AqyEquipmentTargetDataController extends BaseController
{
    @Autowired
    private IAqyEquipmentTargetDataService aqyEquipmentTargetDataService;

    /**
     * 查询靶标数据列表
     */
    @PreAuthorize("@ss.hasPermi('system:data:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentTargetData aqyEquipmentTargetData)
    {
        startPage();
        List<AqyEquipmentTargetData> list = aqyEquipmentTargetDataService.selectAqyEquipmentTargetDataList(aqyEquipmentTargetData);
        return getDataTable(list);
    }

    /**
     * 导出靶标数据列表
     */
    @PreAuthorize("@ss.hasPermi('system:data:export')")
    @Log(title = "靶标数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentTargetData aqyEquipmentTargetData)
    {
        List<AqyEquipmentTargetData> list = aqyEquipmentTargetDataService.selectAqyEquipmentTargetDataList(aqyEquipmentTargetData);
        ExcelUtil<AqyEquipmentTargetData> util = new ExcelUtil<AqyEquipmentTargetData>(AqyEquipmentTargetData.class);
        util.exportExcel(response, list, "靶标数据数据");
    }

    /**
     * 获取靶标数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:data:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyEquipmentTargetDataService.selectAqyEquipmentTargetDataById(id));
    }

    /**
     * 新增靶标数据
     */
    @PreAuthorize("@ss.hasPermi('system:data:add')")
    @Log(title = "靶标数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentTargetData aqyEquipmentTargetData)
    {
        return toAjax(aqyEquipmentTargetDataService.insertAqyEquipmentTargetData(aqyEquipmentTargetData));
    }

    /**
     * 修改靶标数据
     */
    @PreAuthorize("@ss.hasPermi('system:data:edit')")
    @Log(title = "靶标数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentTargetData aqyEquipmentTargetData)
    {
        return toAjax(aqyEquipmentTargetDataService.updateAqyEquipmentTargetData(aqyEquipmentTargetData));
    }

    /**
     * 删除靶标数据
     */
    @PreAuthorize("@ss.hasPermi('system:data:remove')")
    @Log(title = "靶标数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyEquipmentTargetDataService.deleteAqyEquipmentTargetDataByIds(ids));
    }
}
