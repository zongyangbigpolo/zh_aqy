package com.ruoyi.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.aqy.AqyEquipmentImage;
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
import com.ruoyi.service.IAqyEquipmentImageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 监测设备抓取照片记录Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/aqyEquipmentImage")
public class AqyEquipmentImageController extends BaseController
{
    @Autowired
    private IAqyEquipmentImageService aqyEquipmentImageService;

    /**
     * 查询监测设备抓取照片记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentImage:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentImage aqyEquipmentImage)
    {
        startPage();
        List<AqyEquipmentImage> list = aqyEquipmentImageService.selectAqyEquipmentImageList(aqyEquipmentImage);
        return getDataTable(list);
    }

    /**
     * 导出监测设备抓取照片记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentImage:export')")
    @Log(title = "监测设备抓取照片记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentImage aqyEquipmentImage)
    {
        List<AqyEquipmentImage> list = aqyEquipmentImageService.selectAqyEquipmentImageList(aqyEquipmentImage);
        ExcelUtil<AqyEquipmentImage> util = new ExcelUtil<AqyEquipmentImage>(AqyEquipmentImage.class);
        util.exportExcel(response, list, "监测设备抓取照片记录数据");
    }

    /**
     * 获取监测设备抓取照片记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentImage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyEquipmentImageService.selectAqyEquipmentImageById(id));
    }

    /**
     * 新增监测设备抓取照片记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentImage:add')")
    @Log(title = "监测设备抓取照片记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentImage aqyEquipmentImage)
    {
        return toAjax(aqyEquipmentImageService.insertAqyEquipmentImage(aqyEquipmentImage));
    }

    /**
     * 修改监测设备抓取照片记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentImage:edit')")
    @Log(title = "监测设备抓取照片记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentImage aqyEquipmentImage)
    {
        return toAjax(aqyEquipmentImageService.updateAqyEquipmentImage(aqyEquipmentImage));
    }

    /**
     * 删除监测设备抓取照片记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentImage:remove')")
    @Log(title = "监测设备抓取照片记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyEquipmentImageService.deleteAqyEquipmentImageByIds(ids));
    }
}
