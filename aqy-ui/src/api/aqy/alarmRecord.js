import request from '@/utils/request'

// 查询报警记录列表
export function listAlarmRecord(query) {
  return request({
    url: '/aqy/alarmRecord/list',
    method: 'get',
    params: query
  })
}

// 查询报警记录详细
export function getAlarmRecord(id) {
  return request({
    url: '/aqy/alarmRecord/' + id,
    method: 'get'
  })
}

// 新增报警记录
export function addAlarmRecord(data) {
  return request({
    url: '/aqy/alarmRecord',
    method: 'post',
    data: data
  })
}

// 修改报警记录
export function updateAlarmRecord(data) {
  return request({
    url: '/aqy/alarmRecord',
    method: 'put',
    data: data
  })
}

// 删除报警记录
export function delAlarmRecord(id) {
  return request({
    url: '/aqy/alarmRecord/' + id,
    method: 'delete'
  })
}

// 删除报警记录
export function remedialAlarm(data) {
  return request({
    url: '/aqy/alarmRecord/remedialAlarm',
    method: 'post',
    data: data
  })
}
