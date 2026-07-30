import request from '@/utils/request'

// 查询采集设备的证书文件列表
export function listAqyEquipmentFile(query) {
  return request({
    url: '/aqy/aqyEquipmentFile/list',
    method: 'get',
    params: query
  })
}

// 查询采集设备的证书文件详细
export function getAqyEquipmentFile(id) {
  return request({
    url: '/aqy/aqyEquipmentFile/' + id,
    method: 'get'
  })
}

// 新增采集设备的证书文件
export function addAqyEquipmentFile(data) {
  return request({
    url: '/aqy/aqyEquipmentFile',
    method: 'post',
    data: data
  })
}

// 修改采集设备的证书文件
export function updateAqyEquipmentFile(data) {
  return request({
    url: '/aqy/aqyEquipmentFile',
    method: 'put',
    data: data
  })
}

// 删除采集设备的证书文件
export function delAqyEquipmentFile(id) {
  return request({
    url: '/aqy/aqyEquipmentFile/' + id,
    method: 'delete'
  })
}
