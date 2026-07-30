package com.ruoyi.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.aqy.AqySectionEqmt;
import com.ruoyi.service.IAqySectionEqmtService;
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
 * 断面配置的监测设备类型Controller
 *
 * @author MXJ
 * @date 2024-09-11
 */
@RestController
@RequestMapping("/aqy/aqySectionEqmt")
public class AqySectionEqmtController extends BaseController {
    @Autowired
    private IAqySectionEqmtService aqySectionEqmtService;

    /**
     * 查询断面配置的监测设备类型列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqySectionEqmt:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqySectionEqmt aqySectionEqmt) {
        startPage();
        List<AqySectionEqmt> list = aqySectionEqmtService.selectAqySectionEqmtList(aqySectionEqmt);
        return getDataTable(list);
    }

    /**
     * 导出断面配置的监测设备类型列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqySectionEqmt:export')")
    @Log(title = "断面配置的监测设备类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqySectionEqmt aqySectionEqmt) {
        List<AqySectionEqmt> list = aqySectionEqmtService.selectAqySectionEqmtList(aqySectionEqmt);
        ExcelUtil<AqySectionEqmt> util = new ExcelUtil<AqySectionEqmt>(AqySectionEqmt.class);
        util.exportExcel(response, list, "断面配置的监测设备类型数据");
    }

    /**
     * 获取断面配置的监测设备类型详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqySectionEqmt:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(aqySectionEqmtService.selectAqySectionEqmtById(id));
    }

    /**
     * 新增断面配置的监测设备类型
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqySectionEqmt:add')")
    @Log(title = "断面配置的监测设备类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqySectionEqmt aqySectionEqmt) {
        return toAjax(aqySectionEqmtService.insertAqySectionEqmt(aqySectionEqmt));
    }

    /**
     * 修改断面配置的监测设备类型
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqySectionEqmt:edit')")
    @Log(title = "断面配置的监测设备类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqySectionEqmt aqySectionEqmt) {
        return toAjax(aqySectionEqmtService.updateAqySectionEqmt(aqySectionEqmt));
    }

    /**
     * 删除断面配置的监测设备类型
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqySectionEqmt:remove')")
    @Log(title = "断面配置的监测设备类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aqySectionEqmtService.deleteAqySectionEqmtByIds(ids));
    }

    /**
     * 查询断面配置的监测设备类型列表
     */
    @GetMapping("/listEqmtTypeByProjectId/{projectId}")
    public AjaxResult listEqmtTypeByProjectId(@PathVariable("projectId") Long projectId)
    {
        List<String> list = aqySectionEqmtService.listEqmtTypeByProjectId(projectId);
        return AjaxResult.success().put("items", list);
    }
}
