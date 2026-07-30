import request from '@/utils/request'

// 查询设备类型列表
export function listAqyEquipmentType(query) {
  return request({
    url: '/aqy/aqyEquipmentType/list',
    method: 'get',
    params: query
  })
}

// 查询设备类型详细
export function getAqyEquipmentType(id) {
  return request({
    url: '/aqy/aqyEquipmentType/' + id,
    method: 'get'
  })
}

// 新增设备类型
export function addAqyEquipmentType(data) {
  return request({
    url: '/aqy/aqyEquipmentType',
    method: 'post',
    data: data
  })
}

// 修改设备类型
export function updateAqyEquipmentType(data) {
  return request({
    url: '/aqy/aqyEquipmentType',
    method: 'put',
    data: data
  })
}

// 删除设备类型
export function delAqyEquipmentType(id) {
  return request({
    url: '/aqy/aqyEquipmentType/' + id,
    method: 'delete'
  })
}
