package com.ruoyi.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.aqy.AqyHikWy;
import com.ruoyi.common.core.domain.aqy.AqySection;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.hikapi.GNSSApi;
import com.ruoyi.common.hikapi.Params;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.service.IAqyHikWyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author DQC
 * @email 1183158200@qq.com
 * @date 2024/9/26 16:14
 */
@RestController
@RequestMapping("/aqy/hikgnss")
public class HikGnssController  extends BaseController {
    @Autowired
    private IAqyHikWyService aqyHikWyService;


    /**
     * 查询海康位移数据列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:hikgnss:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyHikWy aqyHikWy)
    {
        startPage();
        List<AqyHikWy> list = aqyHikWyService.selectAqyHikWyList(aqyHikWy);
        return getDataTable(list);
    }

    /**
     * 导出海康位移数据列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:hikgnss:export')")
    @Log(title = "海康位移数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyHikWy aqyHikWy)
    {
        List<AqyHikWy> list = aqyHikWyService.selectAqyHikWyList(aqyHikWy);
        ExcelUtil<AqyHikWy> util = new ExcelUtil<AqyHikWy>(AqyHikWy.class);
        util.exportExcel(response, list, "海康位移数据数据");
    }

    /**
     * 获取海康位移数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:hikgnss:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyHikWyService.selectAqyHikWyById(id));
    }

    /**
     * 新增海康位移数据
     */
    @PreAuthorize("@ss.hasPermi('aqy:hikgnss:add')")
    @Log(title = "海康位移数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyHikWy aqyHikWy)
    {
        return toAjax(aqyHikWyService.insertAqyHikWy(aqyHikWy));
    }

    /**
     * 修改海康位移数据
     */
    @PreAuthorize("@ss.hasPermi('aqy:hikgnss:edit')")
    @Log(title = "海康位移数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyHikWy aqyHikWy)
    {
        return toAjax(aqyHikWyService.updateAqyHikWy(aqyHikWy));
    }

    /**
     * 删除海康位移数据
     */
    @PreAuthorize("@ss.hasPermi('aqy:hikgnss:remove')")
    @Log(title = "海康位移数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyHikWyService.deleteAqyHikWyByIds(ids));
    }
    /**
     * 获取接口调用令牌
     */
    @PostMapping("/getAccessToken")
    public AjaxResult getAccessToken()
    {
        return AjaxResult.success(GNSSApi.getAccessToken());
    }


    /**
     * 查询设备详情
     */
    @PostMapping("/byDeviceSerial")
    public AjaxResult byDeviceSerial(@RequestBody Params params)
    {

        return AjaxResult.success(GNSSApi.byDeviceSerial(params));
    }
    /**
     * 获取Web端视频监控画面的地址
     */
    @PostMapping("/getWebVideoUrl")
    public AjaxResult getWebVideoUrl(@RequestBody Params params)
    {
        return AjaxResult.success(GNSSApi.getWebVideoUrl(params));
    }

}
