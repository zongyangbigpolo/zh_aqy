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
import com.ruoyi.common.core.domain.aqy.AqyMqttCmdMessage;
import com.ruoyi.service.IAqyMqttCmdMessageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 记录发送到智能网关的信息Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/mqttCmdMessage")
public class AqyMqttCmdMessageController extends BaseController
{
    @Autowired
    private IAqyMqttCmdMessageService aqyMqttCmdMessageService;

    /**
     * 查询记录发送到智能网关的信息列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:mqttCmdMessage:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyMqttCmdMessage aqyMqttCmdMessage)
    {
        startPage();
        List<AqyMqttCmdMessage> list = aqyMqttCmdMessageService.selectAqyMqttCmdMessageList(aqyMqttCmdMessage);
        return getDataTable(list);
    }

    /**
     * 导出记录发送到智能网关的信息列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:mqttCmdMessage:export')")
    @Log(title = "记录发送到智能网关的信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyMqttCmdMessage aqyMqttCmdMessage)
    {
        List<AqyMqttCmdMessage> list = aqyMqttCmdMessageService.selectAqyMqttCmdMessageList(aqyMqttCmdMessage);
        ExcelUtil<AqyMqttCmdMessage> util = new ExcelUtil<AqyMqttCmdMessage>(AqyMqttCmdMessage.class);
        util.exportExcel(response, list, "记录发送到智能网关的信息数据");
    }

    /**
     * 获取记录发送到智能网关的信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:mqttCmdMessage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyMqttCmdMessageService.selectAqyMqttCmdMessageById(id));
    }

    /**
     * 新增记录发送到智能网关的信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:mqttCmdMessage:add')")
    @Log(title = "记录发送到智能网关的信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyMqttCmdMessage aqyMqttCmdMessage)
    {
        return toAjax(aqyMqttCmdMessageService.insertAqyMqttCmdMessage(aqyMqttCmdMessage));
    }

    /**
     * 修改记录发送到智能网关的信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:mqttCmdMessage:edit')")
    @Log(title = "记录发送到智能网关的信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyMqttCmdMessage aqyMqttCmdMessage)
    {
        return toAjax(aqyMqttCmdMessageService.updateAqyMqttCmdMessage(aqyMqttCmdMessage));
    }

    /**
     * 删除记录发送到智能网关的信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:mqttCmdMessage:remove')")
    @Log(title = "记录发送到智能网关的信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyMqttCmdMessageService.deleteAqyMqttCmdMessageByIds(ids));
    }
}
