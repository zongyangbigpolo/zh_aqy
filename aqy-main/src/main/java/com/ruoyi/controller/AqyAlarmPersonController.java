package com.ruoyi.controller;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.aqy.Vo.AqyAlarmSmsUserVo;
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
import com.ruoyi.common.core.domain.aqy.AqyAlarmPerson;
import com.ruoyi.service.IAqyAlarmPersonService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.ISysUserService;

/**
 * 报警联系人Controller
 *
 * @author MXJ
 * @date 2024-10-13
 */
@RestController
@RequestMapping("/aqy/alarmPerson")
public class AqyAlarmPersonController extends BaseController
{
    @Autowired
    private IAqyAlarmPersonService aqyAlarmPersonService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询报警联系人列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmPerson:list')")
    @GetMapping("/list")
    public TableDataInfo list(AqyAlarmPerson aqyAlarmPerson)
    {
        startPage();
        List<AqyAlarmPerson> list = aqyAlarmPersonService.selectAqyAlarmPersonList(aqyAlarmPerson);
        return getDataTable(list);
    }

    /**
     * 查询可配置为报警短信接收人的系统用户。
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmPerson:list')")
    @GetMapping("/userOptions")
    public AjaxResult userOptions(SysUser sysUser)
    {
        sysUser.setStatus("0");
        List<AqyAlarmSmsUserVo> users = sysUserService.selectUserList(sysUser).stream()
                .filter(user -> user.getPhonenumber() != null && !"".equals(user.getPhonenumber()))
                .map(this::toAlarmSmsUserVo)
                .collect(Collectors.toList());
        return success(users);
    }

    private AqyAlarmSmsUserVo toAlarmSmsUserVo(SysUser user) {
        AqyAlarmSmsUserVo vo = new AqyAlarmSmsUserVo();
        vo.setUserId(user.getUserId());
        vo.setUserName(user.getUserName());
        vo.setNickName(user.getNickName());
        vo.setPhonenumber(user.getPhonenumber());
        return vo;
    }

    /**
     * 导出报警联系人列表
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmPerson:export')")
    @Log(title = "报警联系人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AqyAlarmPerson aqyAlarmPerson)
    {
        List<AqyAlarmPerson> list = aqyAlarmPersonService.selectAqyAlarmPersonList(aqyAlarmPerson);
        ExcelUtil<AqyAlarmPerson> util = new ExcelUtil<AqyAlarmPerson>(AqyAlarmPerson.class);
        util.exportExcel(response, list, "报警联系人数据");
    }

    /**
     * 获取报警联系人详细信息
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmPerson:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(aqyAlarmPersonService.selectAqyAlarmPersonById(id));
    }

    /**
     * 新增报警联系人
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmPerson:add')")
    @Log(title = "报警联系人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AqyAlarmPerson aqyAlarmPerson)
    {
        return toAjax(aqyAlarmPersonService.insertAqyAlarmPerson(aqyAlarmPerson));
    }

    /**
     * 修改报警联系人
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmPerson:edit')")
    @Log(title = "报警联系人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AqyAlarmPerson aqyAlarmPerson)
    {
        return toAjax(aqyAlarmPersonService.updateAqyAlarmPerson(aqyAlarmPerson));
    }

    /**
     * 删除报警联系人
     */
    @PreAuthorize("@ss.hasPermi('aqy:alarmPerson:remove')")
    @Log(title = "报警联系人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(aqyAlarmPersonService.deleteAqyAlarmPersonByIds(ids));
    }
}
