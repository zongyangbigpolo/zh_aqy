import request from '@/utils/request'

// 查询报警等级列表
export function listAlarmLevel(query) {
  return request({
    url: '/aqy/alarmLevel/list',
    method: 'get',
    params: query
  })
}

// 查询报警等级详细
export function getAlarmLevel(id) {
  return request({
    url: '/aqy/alarmLevel/' + id,
    method: 'get'
  })
}

// 新增报警等级
export function addAlarmLevel(data) {
  return request({
    url: '/aqy/alarmLevel',
    method: 'post',
    data: data
  })
}

// 修改报警等级
export function updateAlarmLevel(data) {
  return request({
    url: '/aqy/alarmLevel',
    method: 'put',
    data: data
  })
}

// 删除报警等级
export function delAlarmLevel(id) {
  return request({
    url: '/aqy/alarmLevel/' + id,
    method: 'delete'
  })
}
