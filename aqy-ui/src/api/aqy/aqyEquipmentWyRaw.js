import request from '@/utils/request'

// 查询位移监测设备上传数据记录列表
export function listAqyEquipmentWyRaw(query) {
  return request({
    url: '/aqy/aqyEquipmentWyRaw/list',
    method: 'get',
    params: query
  })
}

// 查询位移监测设备上传数据记录详细
export function getAqyEquipmentWyRaw(id) {
  return request({
    url: '/aqy/aqyEquipmentWyRaw/' + id,
    method: 'get'
  })
}

// 新增位移监测设备上传数据记录
export function addAqyEquipmentWyRaw(data) {
  return request({
    url: '/aqy/aqyEquipmentWyRaw',
    method: 'post',
    data: data
  })
}

// 修改位移监测设备上传数据记录
export function updateAqyEquipmentWyRaw(data) {
  return request({
    url: '/aqy/aqyEquipmentWyRaw',
    method: 'put',
    data: data
  })
}

// 删除位移监测设备上传数据记录
export function delAqyEquipmentWyRaw(id) {
  return request({
    url: '/aqy/aqyEquipmentWyRaw/' + id,
    method: 'delete'
  })
}

// 获取位移数据用于前端折线图显示
export function listWyRawForCharts(query) {
  return request({
    url: '/aqy/aqyEquipmentWyRaw/listWyRawForCharts',
    method: 'get',
    params: query
  })
}
