import request from '@/utils/request'

// 查询裂缝监测设备上传数据记录列表
export function listAqyEquipmentLfRaw(query) {
  return request({
    url: '/aqy/aqyEquipmentLfRaw/list',
    method: 'get',
    params: query
  })
}

// 查询裂缝监测设备上传数据记录详细
export function getAqyEquipmentLfRaw(id) {
  return request({
    url: '/aqy/aqyEquipmentLfRaw/' + id,
    method: 'get'
  })
}

// 新增裂缝监测设备上传数据记录
export function addAqyEquipmentLfRaw(data) {
  return request({
    url: '/aqy/aqyEquipmentLfRaw',
    method: 'post',
    data: data
  })
}

// 修改裂缝监测设备上传数据记录
export function updateAqyEquipmentLfRaw(data) {
  return request({
    url: '/aqy/aqyEquipmentLfRaw',
    method: 'put',
    data: data
  })
}

// 删除裂缝监测设备上传数据记录
export function delAqyEquipmentLfRaw(id) {
  return request({
    url: '/aqy/aqyEquipmentLfRaw/' + id,
    method: 'delete'
  })
}

// 获取位移数据用于前端折线图显示
export function listLfRawForCharts(query) {
  return request({
    url: '/aqy/aqyEquipmentLfRaw/listLfRawForCharts',
    method: 'get',
    params: query
  })
}
