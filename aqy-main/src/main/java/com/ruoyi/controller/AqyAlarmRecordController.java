package com.ruoyi.controller;

import java.beans.Transient;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.websocket.WebSocketUsers;
import com.ruoyi.service.IAqyEquipmentService;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import com.ruoyi.common.core.domain.aqy.AqyAlarmRecord;
import com.ruoyi.service.IAqyAlarmRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报警记录Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/alarmRecord")
public class AqyAlarmRecordController extends BaseController {
    @Autowired
    private IAqyAlarmRecordService aqyAlarmRecordService;
    @Autowired
    private IAqyEquipmentService aqyEquipmentService;

    /**
     * 查询报警记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyAlarmRecord aqyAlarmRecord) {
        startPage();
        List<AqyAlarmRecord> list = aqyAlarmRecordService.selectAqyAlarmRecordList(aqyAlarmRecord);
        return getDataTable(list);
    }

    /**
     * 导出报警记录列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmRecord:export')")
    @Log(title = "报警记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyAlarmRecord aqyAlarmRecord) {
        List<AqyAlarmRecord> list = aqyAlarmRecordService.selectAqyAlarmRecordList(aqyAlarmRecord);
        ExcelUtil<AqyAlarmRecord> util = new ExcelUtil<AqyAlarmRecord>(AqyAlarmRecord.class);
        util.exportExcel(response, list, "报警记录数据");
    }

    /**
     * 获取报警记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmRecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(aqyAlarmRecordService.selectAqyAlarmRecordById(id));
    }

    /**
     * 新增报警记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmRecord:add')")
    @Log(title = "报警记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyAlarmRecord aqyAlarmRecord) {
        return toAjax(aqyAlarmRecordService.insertAqyAlarmRecord(aqyAlarmRecord));
    }

    /**
     * 修改报警记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmRecord:edit')")
    @Log(title = "报警记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyAlarmRecord aqyAlarmRecord) {
        aqyAlarmRecord.setRemedialTime(DateUtils.getNowDate());
        aqyAlarmRecord.setRemedialUid(getUserId());
        return toAjax(aqyAlarmRecordService.updateAqyAlarmRecord(aqyAlarmRecord));
    }

    /**
     * 删除报警记录
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmRecord:remove')")
    @Log(title = "报警记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aqyAlarmRecordService.deleteAqyAlarmRecordByIds(ids));
    }

    /**
     * 人工处理报警
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmRecord:remedial')")
    @Log(title = "人工处理报警")
    @PostMapping("/remedialAlarm")
    @Transactional
    public AjaxResult remedialAlarm(@RequestBody AqyAlarmRecord aqyAlarmRecord){
        AqyEquipment aqyEquipment = aqyEquipmentService.selectAqyEquipmentById(aqyAlarmRecord.getQmtId());
        aqyEquipment.setAlarmLevel(0L);
        aqyEquipmentService.updateAqyEquipment(aqyEquipment);

        aqyAlarmRecord.setRemedialTime(DateUtils.getNowDate());
        aqyAlarmRecord.setRemedialUid(getUserId());
        aqyAlarmRecordService.updateAqyAlarmRecord(aqyAlarmRecord);

        // 更新前端数据
        WebSocketUsers.pushMessage(6, -1, null);
        return AjaxResult.success();
    }
}
