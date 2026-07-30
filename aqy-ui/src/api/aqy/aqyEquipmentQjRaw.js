import request from '@/utils/request'

// 查询倾角监测设备上传数据记录列表
export function listAqyEquipmentQjRaw(query) {
  return request({
    url: '/aqy/aqyEquipmentQjRaw/list',
    method: 'get',
    params: query
  })
}

// 查询倾角监测设备上传数据记录详细
export function getAqyEquipmentQjRaw(id) {
  return request({
    url: '/aqy/aqyEquipmentQjRaw/' + id,
    method: 'get'
  })
}

// 新增倾角监测设备上传数据记录
export function addAqyEquipmentQjRaw(data) {
  return request({
    url: '/aqy/aqyEquipmentQjRaw',
    method: 'post',
    data: data
  })
}

// 修改倾角监测设备上传数据记录
export function updateAqyEquipmentQjRaw(data) {
  return request({
    url: '/aqy/aqyEquipmentQjRaw',
    method: 'put',
    data: data
  })
}

// 删除倾角监测设备上传数据记录
export function delAqyEquipmentQjRaw(id) {
  return request({
    url: '/aqy/aqyEquipmentQjRaw/' + id,
    method: 'delete'
  })
}

// 获取位移数据用于前端折线图显示
export function listQjRawForCharts(query) {
  return request({
    url: '/aqy/aqyEquipmentQjRaw/listQjRawForCharts',
    method: 'get',
    params: query
  })
}
