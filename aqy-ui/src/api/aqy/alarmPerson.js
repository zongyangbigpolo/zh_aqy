import request from '@/utils/request'

// 查询报警联系人列表
export function listAlarmPerson(query) {
  return request({
    url: '/aqy/alarmPerson/list',
    method: 'get',
    params: query
  })
}

// 查询可接收报警短信的系统用户
export function listAlarmSmsUsers(query) {
  return request({
    url: '/aqy/alarmPerson/userOptions',
    method: 'get',
    params: query
  })
}

// 查询报警联系人详细
export function getAlarmPerson(id) {
  return request({
    url: '/aqy/alarmPerson/' + id,
    method: 'get'
  })
}

// 新增报警联系人
export function addAlarmPerson(data) {
  return request({
    url: '/aqy/alarmPerson',
    method: 'post',
    data: data
  })
}

// 修改报警联系人
export function updateAlarmPerson(data) {
  return request({
    url: '/aqy/alarmPerson',
    method: 'put',
    data: data
  })
}

// 删除报警联系人
export function delAlarmPerson(id) {
  return request({
    url: '/aqy/alarmPerson/' + id,
    method: 'delete'
  })
}
