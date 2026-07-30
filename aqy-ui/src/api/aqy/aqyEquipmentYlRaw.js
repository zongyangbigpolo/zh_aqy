import request from '@/utils/request'

// 查询雨量监测设备上传数据记录列表
export function listAqyEquipmentYlRaw(query) {
  return request({
    url: '/aqy/aqyEquipmentYlRaw/list',
    method: 'get',
    params: query
  })
}

// 查询雨量监测设备上传数据记录详细
export function getAqyEquipmentYlRaw(id) {
  return request({
    url: '/aqy/aqyEquipmentYlRaw/' + id,
    method: 'get'
  })
}

// 新增雨量监测设备上传数据记录
export function addAqyEquipmentYlRaw(data) {
  return request({
    url: '/aqy/aqyEquipmentYlRaw',
    method: 'post',
    data: data
  })
}

// 修改雨量监测设备上传数据记录
export function updateAqyEquipmentYlRaw(data) {
  return request({
    url: '/aqy/aqyEquipmentYlRaw',
    method: 'put',
    data: data
  })
}

// 删除雨量监测设备上传数据记录
export function delAqyEquipmentYlRaw(id) {
  return request({
    url: '/aqy/aqyEquipmentYlRaw/' + id,
    method: 'delete'
  })
}

// 获取位移数据用于前端折线图显示
export function listYlRawForCharts(query) {
  return request({
    url: '/aqy/aqyEquipmentYlRaw/listYlRawForCharts',
    method: 'get',
    params: query
  })
}
