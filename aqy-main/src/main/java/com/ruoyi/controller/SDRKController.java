package com.ruoyi.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.aqy.AqySdrkWy;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.sdrkapi.SDRKApi;
import com.ruoyi.common.tongganyunapi.TGYApi;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.service.IAqySdrkWyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2025/2/21 16:57
 */
@RestController
@RequestMapping("/aqy/sdrkwy")
public class SDRKController  extends BaseController {



    @Autowired
    private IAqySdrkWyService aqySdrkWyService;

    /**
     * 查询山东仁科位移数据列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:sdrkwy:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqySdrkWy aqySdrkWy)
    {
        startPage();
        List<AqySdrkWy> list = aqySdrkWyService.selectAqySdrkWyList(aqySdrkWy);
        return getDataTable(list);
    }

    /**
     * 导出山东仁科位移数据列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:sdrkwy:export')")
    @Log(title = "山东仁科位移数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqySdrkWy aqySdrkWy)
    {
        List<AqySdrkWy> list = aqySdrkWyService.selectAqySdrkWyList(aqySdrkWy);
        ExcelUtil<AqySdrkWy> util = new ExcelUtil<AqySdrkWy>(AqySdrkWy.class);
        util.exportExcel(response, list, "山东仁科位移数据数据");
    }

    /**
     * 获取山东仁科位移数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:sdrkwy:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqySdrkWyService.selectAqySdrkWyById(id));
    }

    /**
     * 新增山东仁科位移数据
     */
    @PreAuthorize("@ss.hasPermi('aqy:sdrkwy:add')")
    @Log(title = "山东仁科位移数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqySdrkWy aqySdrkWy)
    {
        return toAjax(aqySdrkWyService.insertAqySdrkWy(aqySdrkWy));
    }

    /**
     * 修改山东仁科位移数据
     */
    @PreAuthorize("@ss.hasPermi('aqy:sdrkwy:edit')")
    @Log(title = "山东仁科位移数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqySdrkWy aqySdrkWy)
    {
        return toAjax(aqySdrkWyService.updateAqySdrkWy(aqySdrkWy));
    }

    /**
     * 删除山东仁科位移数据
     */
    @PreAuthorize("@ss.hasPermi('aqy:sdrkwy:remove')")
    @Log(title = "山东仁科位移数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqySdrkWyService.deleteAqySdrkWyByIds(ids));
    }

    /**
     * 获取接口调用令牌
     */
    @PostMapping("/getAccessToken")
    public AjaxResult getAccessToken() throws UnsupportedEncodingException {
        return AjaxResult.success(SDRKApi.getAccessToken());
    }

    @GetMapping("/getRealTimeDataByDeviceAddr/{deviceAddr}")
    public AjaxResult getRealTimeDataByDeviceAddr(@PathVariable String deviceAddr) throws UnsupportedEncodingException {
        return AjaxResult.success(SDRKApi.getRealTimeDataByDeviceAddr(deviceAddr));
    }
}
