import request from '@/utils/request'

// 查询声光报警设备列表
export function listAlarmEquipment(query) {
  return request({
    url: '/aqy/alarmEquipment/list',
    method: 'get',
    params: query
  })
}

// 查询声光报警设备详细
export function getAlarmEquipment(id) {
  return request({
    url: '/aqy/alarmEquipment/' + id,
    method: 'get'
  })
}

// 新增声光报警设备
export function addAlarmEquipment(data) {
  return request({
    url: '/aqy/alarmEquipment',
    method: 'post',
    data: data
  })
}

// 修改声光报警设备
export function updateAlarmEquipment(data) {
  return request({
    url: '/aqy/alarmEquipment',
    method: 'put',
    data: data
  })
}

// 删除声光报警设备
export function delAlarmEquipment(id) {
  return request({
    url: '/aqy/alarmEquipment/' + id,
    method: 'delete'
  })
}
