import request from '@/utils/request'

// 查询海康位移数据列表
export function listWy(query) {
  return request({
    url: '/aqy/hikgnss/list',
    method: 'get',
    params: query
  })
}

// 查询海康位移数据详细
export function getWy(id) {
  return request({
    url: '/aqy/hikgnss/' + id,
    method: 'get'
  })
}

// 新增海康位移数据
export function addWy(data) {
  return request({
    url: '/aqy/hikgnss',
    method: 'post',
    data: data
  })
}

// 修改海康位移数据
export function updateWy(data) {
  return request({
    url: '/aqy/hikgnss',
    method: 'put',
    data: data
  })
}

// 删除海康位移数据
export function delWy(id) {
  return request({
    url: '/aqy/hikgnss/' + id,
    method: 'delete'
  })
}
