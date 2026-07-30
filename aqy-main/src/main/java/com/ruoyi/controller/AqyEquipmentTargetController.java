package com.ruoyi.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentTarget;
import com.ruoyi.service.IAqyEquipmentTargetService;
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
 * 靶标管理Controller
 * 
 * @author ruoyi
 * @date 2024-12-20
 */
@RestController
@RequestMapping("/aqy/equipmenttarget")
public class AqyEquipmentTargetController extends BaseController
{
    @Autowired
    private IAqyEquipmentTargetService aqyEquipmentTargetService;

    /**
     * 查询靶标管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:target:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentTarget aqyEquipmentTarget)
    {
        startPage();
        List<AqyEquipmentTarget> list = aqyEquipmentTargetService.selectAqyEquipmentTargetList(aqyEquipmentTarget);
        return getDataTable(list);
    }

    /**
     * 导出靶标管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:target:export')")
    @Log(title = "靶标管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentTarget aqyEquipmentTarget)
    {
        List<AqyEquipmentTarget> list = aqyEquipmentTargetService.selectAqyEquipmentTargetList(aqyEquipmentTarget);
        ExcelUtil<AqyEquipmentTarget> util = new ExcelUtil<AqyEquipmentTarget>(AqyEquipmentTarget.class);
        util.exportExcel(response, list, "靶标管理数据");
    }

    /**
     * 获取靶标管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:target:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyEquipmentTargetService.selectAqyEquipmentTargetById(id));
    }

    /**
     * 新增靶标管理
     */
    @PreAuthorize("@ss.hasPermi('system:target:add')")
    @Log(title = "靶标管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentTarget aqyEquipmentTarget)
    {
        return toAjax(aqyEquipmentTargetService.insertAqyEquipmentTarget(aqyEquipmentTarget));
    }

    /**
     * 修改靶标管理
     */
    @PreAuthorize("@ss.hasPermi('system:target:edit')")
    @Log(title = "靶标管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentTarget aqyEquipmentTarget)
    {
        return toAjax(aqyEquipmentTargetService.updateAqyEquipmentTarget(aqyEquipmentTarget));
    }

    /**
     * 删除靶标管理
     */
    @PreAuthorize("@ss.hasPermi('system:target:remove')")
    @Log(title = "靶标管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyEquipmentTargetService.deleteAqyEquipmentTargetByIds(ids));
    }
}
