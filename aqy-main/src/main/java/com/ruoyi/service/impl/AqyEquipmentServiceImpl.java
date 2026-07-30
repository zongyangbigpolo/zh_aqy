package com.ruoyi.service.impl;

import java.util.List;

import cn.hutool.core.util.ArrayUtil;
import com.ruoyi.common.core.domain.aqy.AqyEquipment;
import com.ruoyi.common.core.domain.aqy.DevType;
import com.ruoyi.common.core.domain.aqy.Vo.AqyEqmtAlarmVo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.AqyEquipmentMapper;
import com.ruoyi.service.IAqyEquipmentService;

/**
 * 数据采集设备Service业务层处理
 *
 * @author MXJ
 * @date 2024-10-13
 */
@Service
public class AqyEquipmentServiceImpl implements IAqyEquipmentService
{
    @Autowired
    private AqyEquipmentMapper aqyEquipmentMapper;

    /**
     * 查询数据采集设备
     *
     * @param id 数据采集设备主键
     * @return 数据采集设备
     */
    @Override
    public AqyEquipment selectAqyEquipmentById(Long id)
    {
        return aqyEquipmentMapper.selectAqyEquipmentById(id);
    }

    /**
     * 查询数据采集设备列表
     *
     * @param aqyEquipment 数据采集设备
     * @return 数据采集设备
     */
    @Override
    public List<AqyEquipment> selectAqyEquipmentList(AqyEquipment aqyEquipment)
    {
        return aqyEquipmentMapper.selectAqyEquipmentList(aqyEquipment);
    }

    /**
     * 新增数据采集设备
     *
     * @param aqyEquipment 数据采集设备
     * @return 结果
     */
    @Override
    public int insertAqyEquipment(AqyEquipment aqyEquipment)
    {
        aqyEquipment.setCreateTime(DateUtils.getNowDate());
        return aqyEquipmentMapper.insertAqyEquipment(aqyEquipment);
    }

    /**
     * 修改数据采集设备
     *
     * @param aqyEquipment 数据采集设备
     * @return 结果
     */
    @Override
    public int updateAqyEquipment(AqyEquipment aqyEquipment)
    {
        return aqyEquipmentMapper.updateAqyEquipment(aqyEquipment);
    }

    /**
     * 批量删除数据采集设备
     *
     * @param ids 需要删除的数据采集设备主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentByIds(Long[] ids)
    {
        return aqyEquipmentMapper.deleteAqyEquipmentByIds(ids);
    }

    /**
     * 删除数据采集设备信息
     *
     * @param id 数据采集设备主键
     * @return 结果
     */
    @Override
    public int deleteAqyEquipmentById(Long id)
    {
        return aqyEquipmentMapper.deleteAqyEquipmentById(id);
    }

    @Override
    public List<AqyEquipment> selectAqyEquipmentByCode(String eqmtCode) {
        return aqyEquipmentMapper.selectAqyEquipmentByCode(eqmtCode);
    }

    @Override
    public List<AqyEqmtAlarmVo> selectAqyEquipmentAlarmStatusList(AqyEquipment aqyEquipment) {
            return aqyEquipmentMapper.selectAqyEquipmentAlarmStatusList(aqyEquipment);
    }

    @Override
    public List<AqyEqmtAlarmVo> selectAqyEquipmentListForReport(AqyEquipment aqyEquipment) {
        return aqyEquipmentMapper.selectAqyEquipmentListForReport(aqyEquipment);
    }

    @Override
    public List<AqyEquipment> selectAqyEqmtsByType(String eqmtType) {
        return aqyEquipmentMapper.selectAqyEqmtsByType(eqmtType);
    }
}
