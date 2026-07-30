import request from '@/utils/request'

// 查询数据采集设备列表
export function listAqyEquipment(query) {
  return request({
    url: '/aqy/aqyEquipment/list',
    method: 'get',
    params: query
  })
}

// 查询数据采集设备详细
export function getAqyEquipment(id) {
  return request({
    url: '/aqy/aqyEquipment/' + id,
    method: 'get'
  })
}

// 新增数据采集设备
export function addAqyEquipment(data) {
  return request({
    url: '/aqy/aqyEquipment',
    method: 'post',
    data: data
  })
}

// 修改数据采集设备
export function updateAqyEquipment(data) {
  return request({
    url: '/aqy/aqyEquipment',
    method: 'put',
    data: data
  })
}

// 删除数据采集设备
export function delAqyEquipment(id) {
  return request({
    url: '/aqy/aqyEquipment/' + id,
    method: 'delete'
  })
}

// 查询数据采集设备列表
export function listEqmtsGroupByType(query) {
  return request({
    url: '/aqy/aqyEquipment/listEqmtsGroupByType',
    method: 'get',
    params: query
  })
}

// 查询数据采集设备的报警状态
export function queryEquipmentAlarmStatus(query) {
  return request({
    url: '/aqy/aqyEquipment/queryEquipmentAlarmStatus',
    method: 'get',
    params: query
  })
}

// 查询数据采集设备的在线状态
export function selectAqyEquipmentListForReport(query) {
  return request({
    url: '/aqy/aqyEquipment/selectAqyEquipmentListForReport',
    method: 'get',
    params: query
  })
}
