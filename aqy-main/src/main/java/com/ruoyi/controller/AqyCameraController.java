package com.ruoyi.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.aqy.AqyCamera;
import com.ruoyi.service.IAqyCameraService;
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
 * 监控摄像头Controller
 *
 * @author MXJ
 * @date 2024-09-05
 */
@RestController
@RequestMapping("/aqy/camera")
public class AqyCameraController extends BaseController
{
    @Autowired
    private IAqyCameraService aqyCameraService;

    /**
     * 查询监控摄像头列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:camera:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyCamera aqyCamera)
    {
        startPage();
        List<AqyCamera> list = aqyCameraService.selectAqyCameraList(aqyCamera);
        return getDataTable(list);
    }

    /**
     * 导出监控摄像头列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:camera:export')")
    @Log(title = "监控摄像头", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyCamera aqyCamera)
    {
        List<AqyCamera> list = aqyCameraService.selectAqyCameraList(aqyCamera);
        ExcelUtil<AqyCamera> util = new ExcelUtil<AqyCamera>(AqyCamera.class);
        util.exportExcel(response, list, "监控摄像头数据");
    }

    /**
     * 获取监控摄像头详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:camera:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyCameraService.selectAqyCameraById(id));
    }

    /**
     * 新增监控摄像头
     */
    @PreAuthorize("@ss.hasPermi('aqy:camera:add')")
    @Log(title = "监控摄像头", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyCamera aqyCamera)
    {
        return toAjax(aqyCameraService.insertAqyCamera(aqyCamera));
    }

    /**
     * 修改监控摄像头
     */
    @PreAuthorize("@ss.hasPermi('aqy:camera:edit')")
    @Log(title = "监控摄像头", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyCamera aqyCamera)
    {
        return toAjax(aqyCameraService.updateAqyCamera(aqyCamera));
    }

    /**
     * 删除监控摄像头
     */
    @PreAuthorize("@ss.hasPermi('aqy:camera:remove')")
    @Log(title = "监控摄像头", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyCameraService.deleteAqyCameraByIds(ids));
    }
}
