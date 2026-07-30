import request from '@/utils/request'

// 查询智能网关设备列表
export function listGatwayEquipment(query) {
  return request({
    url: '/aqy/gatwayEquipment/list',
    method: 'get',
    params: query
  })
}

// 查询智能网关设备详细
export function getGatwayEquipment(id) {
  return request({
    url: '/aqy/gatwayEquipment/' + id,
    method: 'get'
  })
}

// 新增智能网关设备
export function addGatwayEquipment(data) {
  return request({
    url: '/aqy/gatwayEquipment',
    method: 'post',
    data: data
  })
}

// 修改智能网关设备
export function updateGatwayEquipment(data) {
  return request({
    url: '/aqy/gatwayEquipment',
    method: 'put',
    data: data
  })
}

// 删除智能网关设备
export function delGatwayEquipment(id) {
  return request({
    url: '/aqy/gatwayEquipment/' + id,
    method: 'delete'
  })
}
