package com.ruoyi.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.aqy.AqyProject;
import com.ruoyi.service.IAqyProjectService;
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
 * 工程项目Controller
 * 
 * @author MXJ
 * @date 2024-09-11
 */
@RestController
@RequestMapping("/aqy/project")
public class AqyProjectController extends BaseController
{
    @Autowired
    private IAqyProjectService aqyProjectService;

    /**
     * 查询工程项目列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyProject aqyProject)
    {
        startPage();
        List<AqyProject> list = aqyProjectService.selectAqyProjectList(aqyProject);
        return getDataTable(list);
    }

    /**
     * 导出工程项目列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:project:export')")
    @Log(title = "工程项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyProject aqyProject)
    {
        List<AqyProject> list = aqyProjectService.selectAqyProjectList(aqyProject);
        ExcelUtil<AqyProject> util = new ExcelUtil<AqyProject>(AqyProject.class);
        util.exportExcel(response, list, "工程项目数据");
    }

    /**
     * 获取工程项目详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:project:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyProjectService.selectAqyProjectById(id));
    }

    /**
     * 新增工程项目
     */
    @PreAuthorize("@ss.hasPermi('aqy:project:add')")
    @Log(title = "工程项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyProject aqyProject)
    {
        return toAjax(aqyProjectService.insertAqyProject(aqyProject));
    }

    /**
     * 修改工程项目
     */
    @PreAuthorize("@ss.hasPermi('aqy:project:edit')")
    @Log(title = "工程项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyProject aqyProject)
    {
        return toAjax(aqyProjectService.updateAqyProject(aqyProject));
    }

    /**
     * 删除工程项目
     */
    @PreAuthorize("@ss.hasPermi('aqy:project:remove')")
    @Log(title = "工程项目", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyProjectService.deleteAqyProjectByIds(ids));
    }
}
