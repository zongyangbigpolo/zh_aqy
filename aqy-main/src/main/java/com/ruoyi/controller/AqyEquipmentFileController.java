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
import com.ruoyi.common.core.domain.aqy.AqyEquipmentFile;
import com.ruoyi.service.IAqyEquipmentFileService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 采集设备的证书文件Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/aqyEquipmentFile")
public class AqyEquipmentFileController extends BaseController
{
    @Autowired
    private IAqyEquipmentFileService aqyEquipmentFileService;

    /**
     * 查询采集设备的证书文件列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentFile:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyEquipmentFile aqyEquipmentFile)
    {
        startPage();
        List<AqyEquipmentFile> list = aqyEquipmentFileService.selectAqyEquipmentFileList(aqyEquipmentFile);
        return getDataTable(list);
    }

    /**
     * 导出采集设备的证书文件列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentFile:export')")
    @Log(title = "采集设备的证书文件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyEquipmentFile aqyEquipmentFile)
    {
        List<AqyEquipmentFile> list = aqyEquipmentFileService.selectAqyEquipmentFileList(aqyEquipmentFile);
        ExcelUtil<AqyEquipmentFile> util = new ExcelUtil<AqyEquipmentFile>(AqyEquipmentFile.class);
        util.exportExcel(response, list, "采集设备的证书文件数据");
    }

    /**
     * 获取采集设备的证书文件详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentFile:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyEquipmentFileService.selectAqyEquipmentFileById(id));
    }

    /**
     * 新增采集设备的证书文件
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentFile:add')")
    @Log(title = "采集设备的证书文件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyEquipmentFile aqyEquipmentFile)
    {
        return toAjax(aqyEquipmentFileService.insertAqyEquipmentFile(aqyEquipmentFile));
    }

    /**
     * 修改采集设备的证书文件
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentFile:edit')")
    @Log(title = "采集设备的证书文件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyEquipmentFile aqyEquipmentFile)
    {
        return toAjax(aqyEquipmentFileService.updateAqyEquipmentFile(aqyEquipmentFile));
    }

    /**
     * 删除采集设备的证书文件
     */
    @PreAuthorize("@ss.hasPermi('aqy:aqyEquipmentFile:remove')")
    @Log(title = "采集设备的证书文件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyEquipmentFileService.deleteAqyEquipmentFileByIds(ids));
    }
}
