import request from '@/utils/request'

// 查询记录发送到智能网关的信息列表
export function listMqttCmdMessage(query) {
  return request({
    url: '/aqy/mqttCmdMessage/list',
    method: 'get',
    params: query
  })
}

// 查询记录发送到智能网关的信息详细
export function getMqttCmdMessage(id) {
  return request({
    url: '/aqy/mqttCmdMessage/' + id,
    method: 'get'
  })
}

// 新增记录发送到智能网关的信息
export function addMqttCmdMessage(data) {
  return request({
    url: '/aqy/mqttCmdMessage',
    method: 'post',
    data: data
  })
}

// 修改记录发送到智能网关的信息
export function updateMqttCmdMessage(data) {
  return request({
    url: '/aqy/mqttCmdMessage',
    method: 'put',
    data: data
  })
}

// 删除记录发送到智能网关的信息
export function delMqttCmdMessage(id) {
  return request({
    url: '/aqy/mqttCmdMessage/' + id,
    method: 'delete'
  })
}
