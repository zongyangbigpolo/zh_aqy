package com.ruoyi.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.aqy.AqySection;
import com.ruoyi.common.core.domain.tree.SectionEqmtTree;
import com.ruoyi.common.core.domain.tree.SectionTree;
import com.ruoyi.service.IAqySectionService;
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
 * 断面信息Controller
 *
 * @author MXJ
 * @date 2024-09-11
 */
@RestController
@RequestMapping("/aqy/section")
public class AqySectionController extends BaseController
{
    @Autowired
    private IAqySectionService aqySectionService;

    /**
     * 查询断面信息列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:section:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqySection aqySection)
    {
        startPage();
        List<AqySection> list = aqySectionService.selectAqySectionList(aqySection);
        return getDataTable(list);
    }


    /**
     * 查询断面信息列表树形结构
     */

    @GetMapping("/listTree")
    public TableDataInfo listTree(AqySection aqySection)
    {
        startPage();
        List<SectionTree> list = aqySectionService.selectAqySectionListTree(aqySection);
        return getDataTable(list);
    }

    /**
     * 导出断面信息列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:section:export')")
    @Log(title = "断面信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqySection aqySection)
    {
        List<AqySection> list = aqySectionService.selectAqySectionList(aqySection);
        ExcelUtil<AqySection> util = new ExcelUtil<AqySection>(AqySection.class);
        util.exportExcel(response, list, "断面信息数据");
    }

    /**
     * 获取断面信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:section:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqySectionService.selectAqySectionById(id));
    }

    /**
     * 新增断面信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:section:add')")
    @Log(title = "断面信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqySection aqySection)
    {
        return toAjax(aqySectionService.insertAqySection(aqySection));
    }

    /**
     * 修改断面信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:section:edit')")
    @Log(title = "断面信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqySection aqySection)
    {
        return toAjax(aqySectionService.updateAqySection(aqySection));
    }

    /**
     * 删除断面信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:section:remove')")
    @Log(title = "断面信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqySectionService.deleteAqySectionByIds(ids));
    }
}
